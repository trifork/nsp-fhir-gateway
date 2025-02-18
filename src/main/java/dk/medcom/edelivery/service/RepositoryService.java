package dk.medcom.edelivery.service;

import dk.medcom.edelivery.fhir.IdUtil;
import dk.medcom.edelivery.fhir.MessageBundleFacade;
import dk.medcom.edelivery.jms.Destinations;
import dk.medcom.edelivery.jms.MetadataSender;
import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.model.mapping.PayloadInfoMapper;
import dk.medcom.edelivery.model.mapping.SBDHMetadataMapper;
import dk.medcom.edelivery.model.mapping.SBDPayloadMapper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.jms.Destination;

@Service
public class RepositoryService {

    private static final Logger log = LogManager.getLogger(RepositoryService.class);

    private final MessageBundleFacade messageBundleFacade;
    private final MetadataSender metaDataSender;
    private final Destination destination;
    private final PayloadInfoMapper payloadInfoMapper;

    public RepositoryService(MessageBundleFacade messageBundleFacade, MetadataSender metaDataSender, Destinations destinations, PayloadInfoMapper payloadInfoMapper) {
        this.messageBundleFacade = messageBundleFacade;
        this.metaDataSender = metaDataSender;
        this.destination = new ActiveMQQueue(destinations.getMetadataRegisterInQueue());
        this.payloadInfoMapper = payloadInfoMapper;
    }

    @Transactional
    public void persist(StandardBusinessDocument document) {
        log.debug("Storing document with DocumentId '{}'", () -> document.getStandardBusinessDocumentHeader().getDocumentIdentification().getInstanceIdentifier());
        var bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));
        var savedBundle = messageBundleFacade.saveMessageBundle(bundle);

        Metadata metadata = SBDHMetadataMapper.toMetadata(document.getStandardBusinessDocumentHeader())
                .setPayloadInfo(payloadInfoMapper.calculatePayloadInfo(savedBundle))
                .setUniqueId(IdUtil.toOIDString(savedBundle.getIdElement()));

        metaDataSender.send(metadata, destination);
    }
}
