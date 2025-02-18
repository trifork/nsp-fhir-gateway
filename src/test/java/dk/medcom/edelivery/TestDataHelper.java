package dk.medcom.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.model.mapping.SBDHAdapter;
import dk.medcom.edelivery.model.mapping.SBDMarshaller;
import dk.medcom.edelivery.model.mapping.SBDUnmarshaller;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.UUID;

import static dk.medcom.edelivery.model.ScopeIdentifiers.*;
import static dk.medcom.edelivery.model.ScopeTypes.*;
import static dk.medcom.edelivery.model.ScopeTypes.TYPECODE;

public class TestDataHelper {

    public static StandardBusinessDocumentHeader getStandardBusinessDocumentHeader() {
        ObjectFactory objectFactory = new ObjectFactory();

        var docInstanceId = UUID.randomUUID().toString();

        var standard = "urn:dk:healthcare:medcom:oioxml:schema:xsd:HospitalReferral";

        return objectFactory.createStandardBusinessDocumentHeader()
                .withHeaderVersion("1.0")
                .withSender(new Partner()
                        .withIdentifier(new PartnerIdentification()
                                .withAuthority("iso6523-actorid-upis")
                                .withValue("0088:5790000121526")))
                .withReceiver(new Partner()
                        .withIdentifier(new PartnerIdentification()
                                .withAuthority("iso6523-actorid-upis")
                                .withValue("0088:5790000201389")))
                .withDocumentIdentification(new DocumentIdentification()
                        .withStandard(standard)
                        .withTypeVersion("XH0130R")
                        .withInstanceIdentifier(docInstanceId)
                        .withType("XREF01")
                        .withMultipleType(false)
                        .withCreationDateAndTime(aDate()))
                .withBusinessScope(new BusinessScope()
                        .withScope(
                                scope(DOCUMENTID, DK_MESSAGING_DOCID, "urn:dk:healthcare:medcom:oioxml:schema:xsd:HospitalReferral##urn:www.medcom.dk:messaging:HospitalReferral/Letter/TypeCode/XREF01::HospitalReferral/Letter/VersionCode/XH0130R"),
                                scope(PROCESSID, DK_MESSAGING_PROCID, "urn:www.digst.dk:profile:sdn-emergence"),
                                scope(SENDERID, "Sorkode", "1170101"),
                                scope(RECEIVERID, "Sorkode", "1170102"),
                                scope(STATISTICALCODE, DK_MEDCOM_MESSAGING, "XREF01"),
                                scope(MESSAGEIDENTIFIER, DK_MEDCOM_MESSAGING, UUID.randomUUID().toString()),
                                scope(EPISODEOFCAREIDENTIFIER, DK_MEDCOM_MESSAGING, UUID.randomUUID().toString()),
                                scope(AUTHORINSTITUTION, DK_DDS_AUTHOR_INSTITUTION, "Odense Universitetshospital – Svendborg^^^^^&1.2.208.176.1.1&ISO^^^^8071000016009"),
                                scope(CLASSCODE, DK_DDS_CODE, "005"),
                                scope(CLASSCODE, DK_DDS_CODE_SYSTEM, "1.2.208.184.100.9"),
                                scope(CLASSCODE, DK_DDS_DISPLAY_NAME, "meddelelse"),
                                scope(CONFIDENTIALITYCODE, DK_DDS_CODE, "N"),
                                scope(CONFIDENTIALITYCODE, DK_DDS_CODE_SYSTEM, "2.16.840.1.113883.5.25"),
                                scope(CONFIDENTIALITYCODE, DK_DDS_DISPLAY_NAME, "normal"),
                                scope(CREATIONTIME, DK_DDS_METADATA, "20201106161900"),
                                scope(ENTRYUUID, DK_DDS_METADATA, docInstanceId),
                                scope(FORMATCODE, DK_DDS_CODE, "urn:ad:dk:medcom:message:full"),
                                scope(FORMATCODE, DK_DDS_CODE_SYSTEM, "1.2.208.184.100.10"),
                                scope(FORMATCODE, DK_DDS_DISPLAY_NAME, "dk:medcom:meddelelse"),
                                scope(HEALTHCARE_FACILITY_TYPE_CODE, DK_DDS_CODE, "394761003"),
                                scope(HEALTHCARE_FACILITY_TYPE_CODE, DK_DDS_CODE_SYSTEM, "2.16.840.1.113883.6.96"),
                                scope(HEALTHCARE_FACILITY_TYPE_CODE, DK_DDS_DISPLAY_NAME, "lægepraksis (snomed:  almen lægepraksis)"),
                                scope(INTENDEDRECIPIENT, DK_DDS_METADATA, "1170102"),
                                scope(LANGUAGECODE, DK_DDS_METADATA, "da-DK"),
                                scope(LEGALAUTHENTICATOR, DK_DDS_METADATA, "^Andersen^Anders^Frederik&Ingolf^^^^^^^&ISO"),
                                scope(MIMETYPE, DK_DDS_METADATA, "application/fhir+xml"),
                                scope(OBJECTTYPE, DK_DDS_METADATA, "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1"),
                                scope(PATIENTID, DK_DDS_METADATA, "0506504003^^^&1.2.208.176.1.2&ISO"),
                                scope(PRACTICESETTINGCODE, DK_DDS_METADATA, "N/A"),
                                scope(SOURCEPATIENTID, DK_DDS_METADATA, "0506504003^^^&1.2.208.176.1.2&ISO"),
                                scope(SOURCEPATIENTINFO, DK_DDS_METADATA_PID_5, "^Matthiesen^Tjalfe"),
                                scope(SOURCEPATIENTINFO, DK_DDS_METADATA_PID_7, "19500605"),
                                scope(SOURCEPATIENTINFO, DK_DDS_METADATA_PID_8, "M"),
                                scope(TITLE, DK_DDS_METADATA, standard + " for 0506504003"),
                                scope(TYPECODE, DK_DDS_CODE, "MedCom-OIOXML"),
                                scope(TYPECODE, DK_DDS_CODE_SYSTEM, "2.16.840.1.113883.6.1"),
                                scope(TYPECODE, DK_DDS_DISPLAY_NAME, "MedCom-OIOXML")
                        ));
    }

    public static XMLGregorianCalendar aDate() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(2020, 11, 6, 16, 19, 0, 0, 60);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Scope scope(String type, String identifier, String instanceIdentifier) {
        return new Scope()
                .withType(type)
                .withIdentifier(identifier)
                .withInstanceIdentifier(instanceIdentifier);
    }

    public static void setService(Submission submission, String service) {
        Payload payload = submission.getPayloads().stream().filter(p -> p.getContentId().equals("cid:message")).findFirst().orElseThrow();
        StandardBusinessDocument document = SBDUnmarshaller.unmarshal(payload.getPayloadDatahandler());
        var adapter = SBDHAdapter.from(document.getStandardBusinessDocumentHeader());
        if (document != null && document.getStandardBusinessDocumentHeader() != null) {
            adapter.getScope(PROCESSID).get().setInstanceIdentifier(service);
        }
        submission.getPayloads().clear();
        Payload p = new Payload(payload.getContentId(), SBDMarshaller.marshal(document),
                payload.getPayloadProperties(),
                payload.isInBody(),
                payload.getDescription(),
                payload.getSchemaLocation());
        submission.getPayloads().add(p);
        submission.setService(service);
    }
}
