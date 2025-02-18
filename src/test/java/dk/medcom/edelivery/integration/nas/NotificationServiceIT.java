package dk.medcom.edelivery.integration.nas;

import dk.medcom.edelivery.NasClientFactory;
import dk.medcom.edelivery.integration.dgws.*;
import dk.nsi._2012._12.nas.idlist.CreateIDListRequest;
import dk.nsi._2012._12.nas.idlist.DestroyIDListRequest;
import dk.nsi.advis.v10.IDFilter;
import dk.nsi.advis.v10.IDList;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oasis_open.docs.wsn.b_2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXB;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static dk.medcom.edelivery.integration.nas.NotificationMapper.ID_TYPE_CPR;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableConfigurationProperties({NASConfigurationProperties.class, SOSIConfigurationProperties.class})
@SpringBootTest(
        classes = {NASConfiguration.class, DGWSHeaderFactory.class, SOSIConfiguration.class, SOSIContext.class, SOSIIDCardProviderImpl.class}
)
class NotificationServiceIT {

    public static final String CPR = "0506504003";
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DGWSHeaderFactory dgwsHeaderFactory;

    private NasClientFactory nasClientFactory;

    private W3CEndpointReference pullpointReference;
    private W3CEndpointReference subscriptionReference;
    private String idListName;

    @BeforeEach
    void setUp() throws Exception {
        nasClientFactory = new NasClientFactory("https://nas2-nsp.nsptest.medcom.dk", dgwsHeaderFactory);
        pullpointReference = subscribe();
    }

    @AfterEach
    void tearDown() throws Exception {
        nasClientFactory.notificationProducer(subscriptionReference)
                .unsubscribe(new Unsubscribe());

        nasClientFactory
                .pullPoint(pullpointReference, Collections.singletonMap("SOAPAction", Arrays.asList("http://docs.oasis-open.org/wsn/bw-2/PullPoint/DestroyPullPointRequest")))
                .destroyPullPoint(new DestroyPullPoint());

        DestroyIDListRequest req = new DestroyIDListRequest();
        req.setName(idListName);
        nasClientFactory.idList().destroyIDList(req);
    }

    @Test
    void sendNotificationToNAS() {
        var notification = new Notification()
                .setPersonIdentifier(new PersonIdentifier().setStandard(" 1.2.208.176.1.2").setValue(CPR))
                .setDocumentId(new UUID(132L, 456L).toString())
                .setStandard("urn:dk:healthcare:medcom:oioxml:schema:xsd:HospitalReferral")
                .setOriginalSender("0088:5790000121526")
                .setFinalRecipient("0088:5790000201389")
                .setType("XREF01")
                .setTypeVersion("XH0130R");

        notificationService.send(notification);

        Awaitility.await().atMost(Duration.ofSeconds(10L))
                .untilAsserted(() -> assertTrue(() -> getMessages(pullpointReference).toString().contains(notification.getDocumentId())));
    }

    private W3CEndpointReference subscribe() throws Exception {
        var idList = nasClientFactory.idList();
        var pullPointFactory = nasClientFactory.createPullPoint();
        var notificationProducer = nasClientFactory.notificationProducer();

        idListName = "id-list-mdcom-gateway-test-" + System.currentTimeMillis();
        idList.createIDList(getCreateIDListRequest(idListName));

        var pullPointResponse = pullPointFactory.createPullPoint(new CreatePullPoint());

        var subscribeRequest = new Subscribe();
        subscribeRequest.setConsumerReference(pullPointResponse.getPullPoint());
        FilterType filter = new FilterType();
        subscribeRequest.setFilter(filter);
        TopicExpressionType topicExpression = new TopicExpressionType();
        topicExpression.setDialect(NotificationMapper.SIMPLE_DIALECT_URI);
        topicExpression.getContent().add(NotificationMapper.TOPIC);
        filter.getAny().add(new ObjectFactory().createTopicExpression(topicExpression));
        QueryExpressionType queryExpression = new QueryExpressionType();
        queryExpression.setDialect("http://nsi.dk/advis/v10/IDMCP_Dialect");
        IDFilter idFilter = new IDFilter();
        idFilter.setIDList(new IDList());
        idFilter.getIDList().setName(idListName);
        queryExpression.getContent().add(idFilter);

        filter.getAny().add(new ObjectFactory().createMessageContent(queryExpression));

        var subscribe = notificationProducer.subscribe(subscribeRequest);

        subscriptionReference = subscribe.getSubscriptionReference();

        return pullPointResponse.getPullPoint();
    }

    private List<String> getMessages(W3CEndpointReference pullpointref) {
        try {
            var pullPoint = nasClientFactory.pullPoint(pullpointref, Collections.singletonMap("SOAPAction", Arrays.asList("http://docs.oasis-open.org/wsn/bw-2/PullPoint/GetMessagesRequest")));

            GetMessages getMessages = new GetMessages();
            getMessages.setMaximumNumber(BigInteger.valueOf(20L));
            GetMessagesResponse messages = pullPoint.getMessages(getMessages);
            var notificationMessages = messages.getNotificationMessage();

            return notificationMessages.stream()
                    .map(NotificationMessageHolderType::getMessage)
                    .map(NotificationMessageHolderType.Message::getAny)
                    .map(o -> {
                        StringWriter writer = new StringWriter();
                        JAXB.marshal(o, writer);
                        return writer.toString();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private CreateIDListRequest getCreateIDListRequest(String name) {
        CreateIDListRequest req = new CreateIDListRequest();
        req.setName(name);
        req.setDescription("MedCom Gateway Id list Test");
        req.setIDType(ID_TYPE_CPR);
        req.getId().add(CPR);
        return req;
    }
}
