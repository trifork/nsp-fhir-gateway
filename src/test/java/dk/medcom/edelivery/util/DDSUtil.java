package dk.medcom.edelivery.util;

import dk.medcom.edelivery.integration.dds.DDSConfigurationProperties;
import org.openehealth.ipf.commons.ihe.xds.core.requests.DocumentReference;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.query.AdhocQueryRequest;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.query.ResponseOptionType;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rim.AdhocQueryType;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rim.SlotType1;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rim.ValueListType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

import static dk.medcom.edelivery.util.QueryConstants.*;
import static dk.medcom.edelivery.util.QueryConstants.XDSDocumentEntryServiceStartTimeFrom;

@Service
public class DDSUtil {

    private DDSConfigurationProperties ddsProperties;

    public DDSUtil(DDSConfigurationProperties ddsProperties) {
        this.ddsProperties = ddsProperties;
    }

    public AdhocQueryRequest createITI18QueryFindDocuments(String patientId, String serviceStartTimeFrom) {
        AdhocQueryRequest adhocQueryRequest = new AdhocQueryRequest();
        ResponseOptionType type = new ResponseOptionType();
        type.setReturnType("LeafClass"); // or ObjectRef
        type.setReturnComposedObjects(false);
        adhocQueryRequest.setResponseOption(type);
        adhocQueryRequest.setMaxResults(BigInteger.valueOf(10));
        adhocQueryRequest.setStartIndex(BigInteger.valueOf(10));
        AdhocQueryType adhocQuery = new AdhocQueryType();
        adhocQuery.setId(QUERY_TYPE_FINDDOCUMENTS);
        addSlot(adhocQuery, XDSDocumentEntryPatientId, "'" + patientId + "'");
        addSlot(adhocQuery, XDSDocumentEntryType, ENTRY_TYPE_STABLE_DOCUMENT);
        addSlot(adhocQuery, XDSDocumentEntryStatus, "('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')");
        addSlot(adhocQuery, XDSDocumentEntryServiceStartTimeFrom, serviceStartTimeFrom);
        //addSlot(adhocQuery, "$XDSDocumentEntryUniqueId", "('2.25.53975151192308831184067988640885802778')");
        adhocQueryRequest.setAdhocQuery(adhocQuery);
        return adhocQueryRequest;
    }

    private void addSlot(AdhocQueryType adhocQuery, String name, String value) {
        SlotType1 slot = new SlotType1();
        slot.setName(name);
        ValueListType valueList = new ValueListType();
        valueList.getValue().add(value);
        slot.setValueList(valueList);
        adhocQuery.getSlot().add(slot);
    }

    public RetrieveDocumentSet getRetrieveDocumentSet(String documentUniqueId) {
        RetrieveDocumentSet request = new RetrieveDocumentSet();
        String repositoryUniqueId = ddsProperties.getRepositoryUniqueId();
        String homeCommunityId = ddsProperties.getHomeCommunityId();
        request.getDocuments().add(new DocumentReference(repositoryUniqueId, documentUniqueId, homeCommunityId));
        return request;
    }


}
