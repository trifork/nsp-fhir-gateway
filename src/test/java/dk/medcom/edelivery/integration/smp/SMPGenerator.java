package dk.medcom.edelivery.integration.smp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.medcom.edelivery.TestConstants.TEST_ACTION;
import static dk.medcom.edelivery.integration.edelivery.SubmissionFactory.SBDH_ACKNOWLEDGEMENT_ACTION;
import static dk.medcom.edelivery.integration.edelivery.SubmissionFactory.SBDH_EXCEPTIONACKNOWLEDGEMENT_ACTION;

// Utility for generating SMP configuration scripts
public class SMPGenerator {

    static final int port = 8080;
    static final String smpUsername = "smp";
    static final String smpPassword = "123456";

    static final String processSDNEmergence = "urn:www.digst.dk:profile:sdn-emergence";
    static final String processSDNDistribution = "urn:www.digst.dk:profile:sdn-distribution";
    static final String processFODEmergence = "urn:www.digst.dk:profile:fod-emergence";
    static final String processFODDistribution = "urn:www.digst.dk:profile:fod-distribution";

    static final String CERT_GATEWAY = "MIIFXDCCA0SgAwIBAgIJAL9WEqdLOEA3MA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNV\n" +
            "BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMRgwFgYD\n" +
            "VQQDDA9kb21pYnVzLWdhdGV3YXkwHhcNMjIwMTE3MDk1MjU1WhcNMjcwNzEwMDk1\n" +
            "MjU1WjBLMQswCQYDVQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwH\n" +
            "VHJpZm9yazEYMBYGA1UEAwwPZG9taWJ1cy1nYXRld2F5MIICIjANBgkqhkiG9w0B\n" +
            "AQEFAAOCAg8AMIICCgKCAgEAuVE0158JnBTjs81dxH3CblQwk5OkXdKu2cuSQlMo\n" +
            "QQBspe2Tkiy3VyoHfjk+5QJvPaTSqbcY7Uh1gfSWzNqi3apYHkvbZDngfLAysLMV\n" +
            "fkZQxU72i9N5YH1QLXsxYPu2hV0PtIA1OJEl2MHjJNm3Ka5KNGnq8TgaSir+EVtu\n" +
            "tj0Xa7s4EZYRnw+5cwQVsEXyYLgTOWUb9rcLiK+7Q1xne/SJ6SHox2wqDBKT96Ap\n" +
            "xNtaO2pMDrMYxw+euUFSq9G+QiJoj+yu4E1wrdpSVGo+sFeLRIihpCU59QtbjR/I\n" +
            "MP2qVgit/ETcUnJ/neufjiiexyP1KlO2C3zlMEhcosBdpmA5xTnkjWTWhPWHVBPS\n" +
            "qOnEd8wI7jKMRixGUoUjiQZVahR4yY76g/B1mKmIdvfLMa4yUWlcxfxr020cpAZp\n" +
            "JsyH3xDj8CReldw3Dq3O/NCqUB/RLnRhCmkiDMrCRYbnDiK620ybwclxm9aIvhGE\n" +
            "FJJ2xnhucSeuSB9jhY5wBzYT6sZq4I550SFe+neKiCN4IeuN00lDUccMjijSUKRH\n" +
            "weQg/QxtMj/DDIGFfmwBSJg81Sf9jd2br8STlwBK9o3DI0lmc6NyUK09tAoLEfDf\n" +
            "8zXhbHL6or7NvzKE3m6XYDkLQaIq8EPOHmHa9j2/MemR+Y4TZ40EleVtvFQeFRVK\n" +
            "AyUCAwEAAaNDMEEwDgYDVR0PAQH/BAQDAgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMB\n" +
            "MBoGA1UdEQQTMBGCD3d3dy50cmlmb3JrLmNvbTANBgkqhkiG9w0BAQsFAAOCAgEA\n" +
            "B/jUcsTopr4Vyp2fb0EhLV3mEB9T3m9wJdLIXDtUwF+MbZ7TPyfWJbUymwmgl7eY\n" +
            "1PmQHp69MWKZwimkjRK1B2cs5pV8woGZf/9s8HGpclkvXops5QhZwQdv0NSTS3bl\n" +
            "iyVgvDNa+OMjp0dS7uBdev07qnVHqd4jrRKOtDpthrgLBGEil1u4lSWW8ATccSv6\n" +
            "Puh9SyG+OT5RZrqHFVQhGkn6PK6OiK3bqd29noHq0nK9qE147vnUoLoVQjPAyPgs\n" +
            "Ehm6eVqWVGPIc6609Djki5uIxzV4gicVz2q+aZQ3s/tKAN1UxbkmiMY+jY576L2Q\n" +
            "9dXElBQn3OqzRnr0YlrWRBInafMS8WGdcin6+jK3kYzgjD99MThC9iem431Pjzg5\n" +
            "Ox0vsAG1kNNF6Wmi0FiCejlEvW/0lpWb/foEXBs14E6fGF1jOFVEkqH82fIQt64c\n" +
            "6Egw241LFiNQqbuNDBKS9JKsAkktTQ+jzNLZAPw4qMcI3Q4NIU5zhkzAxHVKOx89\n" +
            "SJRFy66NXhIq+etfRC6B6kPfhpW82mRme8JXXwP00a4VtNrrMbYNC7l2TWpIC6J/\n" +
            "vDeHvoHhJftHkg5NtebN9vqOhNazncWLq7dfgZvaV425W4mZl1R42h9ktMa1Crhk\n" +
            "HBqzfhx8u0GCJwPAVo9AqndV2ahKLH3+WOfiwMGimzw=";
    static final String CERT_AP2 = "MIIFRDCCAyygAwIBAgIJAO3QrImA2f6SMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV\n" +
            "BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD\n" +
            "VQQDDANhcDIwHhcNMjIwMTE3MDk1MzIyWhcNMjcwNzEwMDk1MzIyWjA/MQswCQYD\n" +
            "VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG\n" +
            "A1UEAwwDYXAyMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA3xtqOzJl\n" +
            "58mjnBgs4wABUjKLbNdDPv0UX8iWVa4s6/O1blBcMKKXLxpdNThiYxI6NH1/GcXW\n" +
            "okmoLNMre3lJnn2JWPq3B0YSCioopz46bdNbKrmw66XGRA47Hooge8hCQYZBSvgq\n" +
            "lfvhLAoBsC3PdtZ2bo83K6oE6ffWcb6zkCXwD1U9CvXwR0axOTqz/enLgjKqUB+S\n" +
            "lUHoFV7QEDI1CNskXB0FDUGBACNcXDkQol0Zx3cvsL2TtbRpDeha+knU0dhjztid\n" +
            "TNlwdCPNyCmsN8db6uMUm0vbEu/cv95aYe/zuezHB4+YdmmBscRWWerlEBPC9Np9\n" +
            "NuARVFZlTYRPtIk6iqZvDd+mpESu+kOHrmwAx6HVHUlJZRpHziqY+X8gmfvstEnb\n" +
            "dmFDGH/q+WW+hOUChVO+yu1jyfpPtTucqBrgKHh3ZVEh7b4/3TfJpnPXina/ZRPy\n" +
            "CRN5/sAnQqbYxRwckoeouw0mX+uUbU+RBKCpWcUV0oXU7kaKjMWaJeHw7eqFtvH/\n" +
            "okb7j0sV1mzrJDvYxT/Vl6ESTnBqYNnEiI4ZCpJBncfF6ViDLBjFQOHf5gtvQoNo\n" +
            "Xfb3KuRjbdqSkRKvDmFZjtrrRF/rtmZy7zVxDI6iUaOw3Ff3w/YZrTqAA9ot5ZJ5\n" +
            "VhIw6u2m0MiyjiutwV8C+4YOXEOB3J1Q7vUCAwEAAaNDMEEwDgYDVR0PAQH/BAQD\n" +
            "AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr\n" +
            "LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEASYz+FvLWP+lgcV8ykqRDmGo1zsIJYQzQ\n" +
            "0xeUZyGCjzyVJnXQncROVPKj9Q474E1aD0Rzo7LFIY9FShFEXq7/VzApXkz2xCnV\n" +
            "laNu3RG7Lwq9lrGTKo8s92z4MInLNXDC+z6rUn5GdCGaG7kX9Z7Ua0fjfnATBzf6\n" +
            "2O1pI8okZ5f46Z+tekUoUa+bPZS+ee5GPX09UERL2lz6rCAwGfah14EhOmwuEwZ8\n" +
            "C3hchE4KXJRt+y00T64hvPOch4FFzY6OPwgcLoyOWooa7rv0wqzWZ9Oe7ETlPj2B\n" +
            "dkWsbgJbxns3QZAxNXz+2mgmpssUC4ZQigZs1HkAaboQt+hYkPIni3FWFBGon3ic\n" +
            "FNt0SLgd9ESekZLA+2wZkrRt8hoS+CzYZUzr8ksZUG+kp9rkafU+/JGrjrG9uvZW\n" +
            "eww9RQK/jRd/Ii666cn2GqPsvNyDOqJJuOlgauA+f1ZBwyy7dhzU0zfq2T+kmXF7\n" +
            "w0OQYnQXHsif1vErijGCi3tvrLzZqQcI0kU3zhY+aLtuxoErLqin1acaZ+LQSFgT\n" +
            "ngYNqaYP8wvw7lfR0MHg8J20c2PEt67QbYdUEzYmAzuY2+DanHYKMi+JLW5MVmv1\n" +
            "OHl/itZ9IxjzSckly2wlVI9NhGYvUu/4xtv8md+7uT7z0weJHuDWIZ/4Kyqc3lu8\n" +
            "mi16FSwUefQ=";
    static final String CERT_AP1 = "MIIFRDCCAyygAwIBAgIJAKA2HL5nb4ZTMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV\n" +
            "BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD\n" +
            "VQQDDANhcDEwHhcNMjIwMTE3MDk1MzExWhcNMjcwNzEwMDk1MzExWjA/MQswCQYD\n" +
            "VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG\n" +
            "A1UEAwwDYXAxMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzz+tQ57W\n" +
            "6E/bX0DRmTK29HOJ4nBh+fIuThWeJZJhwUzTXjgNXzTjrztOU9VkDfpD7DCJWTpa\n" +
            "QYZZajvD5t9/y4oeHP/02FR+F9vnPTMVjHQg6lFD+65chWOiq+QyZ4k8thmrnZCG\n" +
            "wRzSBAbJKls5iNohnkChxETb0CnT3Pj61q9Lt5tjDkzIsg9W46x2kjKW4WWzua9r\n" +
            "KxEKs7X+smPjvpW4crkFGCLTdKsQ9TOO7DBMV3V2Q2LjnzBS9JScPB7OkMwseSRO\n" +
            "b++1P9wK54CsPQ4kIKPjGNZmf3sFA0NmhV5PHpDZiwLhJXZukXIJZFGyfOQyVynD\n" +
            "V/PRQJUJf+UdPxJqRALcEne/2W5RC+69nar0mMU7wmcsDhQ0KhOZ2CiU/bsZhmz+\n" +
            "Wi0r3WLQFaDNJIXUjupQoDJH9bMq4LIoOn4JjEaoLGW7TZKazLfRzaVz9EPKYXPr\n" +
            "+AvgXxHMAZx2EfjwZRwJvXg9MnOlv33Kd88QpUXi9mhs8xjETHBeBoQLwrKGYs9Q\n" +
            "2cbAka8S/Y3EtX7dsHNfM09QQ6MX8XD1kZxJISa49h0J7X/u1CtoRzlJmaYNjJSN\n" +
            "KcyoBC1PSF3356TnAslIl74fPVQPT+nsvlwLc4sQge7/T7Mahws/CnKtYnGXIQjm\n" +
            "hugOaSJ0mU1kh6CMeProDCSQURnPF4a/aZkCAwEAAaNDMEEwDgYDVR0PAQH/BAQD\n" +
            "AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr\n" +
            "LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEAOEX7he1Fp1U7HvAKEZLAgYf8lRC1kHQQ\n" +
            "uVluDADJi0wyVYPBXQTdor3Msejl3znUqYjSHJeXKkKPxSPQfRJEq75265LAjAdz\n" +
            "t9WCKnyBMnPvrVoMITDY2OcMALk5dlo7qLPeTWOl6ibVQsAnXlEy31O0KuzNv51h\n" +
            "WS0x3Q/ZkBfjx9NIVgvU9qGqt0bvtuWKyE2j5suROQeBSlb6m7vE2lCH0EoxAehi\n" +
            "b7TebVrw7ohhMwj+hGSdVQTaC+x/PorAyE0e5uZoF1z9K2Kx3I375kDcGHRraw/1\n" +
            "F52RAhoSu7i3CtHr+p0nm5MR4bUgyjXJe+FbaiMmR03XCEo5SAFn74DWLKNeb/va\n" +
            "j6zHtvZpm6sRdydJxuAwBV51C8jqLADu8cgwZVzPUZ8tKRnnvKicNWCXUTo03FLk\n" +
            "2CB19efpvW4rRO/3BozMcG+tm+1vRdT9U81CvsYgH3xyTy0V0uIkGD3I2k1msWM/\n" +
            "G59RxsXY79cDwSjYzJ6b7pxE0rhkPTShIguMJtzjFo/K8dkzfu6A9RfzF8lDbTYN\n" +
            "bzn9VgPDWSlqOw24ljNv3o/z76gT8J/GPabgGJgd4uuy+I5aZC+B9OSHASaQm9ux\n" +
            "wpfT1EZfW4MVfurkw0pifxqMnhCYd0kIeyqHA137Q/MWSiNnim7ELPaM9xsn+cGD\n" +
            "QDvVJEGDqmU=";

    public static final String FOD_EMERGENCE_CERT = "#FOD_EMERGENCE_CERT#";
    public static final String SDN_EMERGENCE_CERT = "#SDN_EMERGENCE_CERT#";
    public static final String FOD_DISTRIBUTION_CERT = "#FOD_DISTRIBUTION_CERT#";
    public static final String SDN_DISTRIBUTION_CERT = "#SDN_DISTRIBUTION_CERT#";
    public static final String OUTPUT_FILE = "src/test/resources/smp/smp-tomcat/medcom-config.sh";

    static String distributionCertificate = CERT_AP2;

    void setCertificates(String recipient) {
        if (recipient.equals(recipientAP1)) {
            distributionCertificate = CERT_AP1;
        }
        if (recipient.equals(recipientAP2)) {
            distributionCertificate = CERT_AP2;
        }
    }

    static String participantScheme = "iso6523-actorid-upis";
    static String medcomDocumentIdentifierScheme = "urn:dk:healthcare:medcom:messaging:oioxml";
    static String ebxmlDocumentIdentifierScheme = "urn:dk:healthcare:medcom:messaging:ebxml";
    static String processIdentifierScheme = "dk-messaging-procid";

    static String HOST_AP1 = "http://domibus-ap1:8080/domibus/services/msh";
    static String GATEWAY_HOST = "http://domibus-gateway:8080/domibus/services/msh";
    static String HOST_AP2 = "http://domibus-ap2:8080/domibus/services/msh";

    //static String recipient = "9902:DK42424242";
    static final String recipientDDS = "0088:dds";
    static final String recipientAP1 = "0088:ap1";
    static final String recipientAP2 = "0088:ap2";
    static Map<String, String[]> gln2DocumentIds;

    static class Recipient {
        public Recipient(String participantGLN, String fodEmergenceURL, String fodDistributionURL, String sdnEmergenceURL, String sdnDistributionURL) {
            this.participantGLN = participantGLN;
            this.sdnEmergenceURL = sdnEmergenceURL;
            this.sdnDistributionURL = sdnDistributionURL;
            this.fodEmergenceURL = fodEmergenceURL;
            this.fodDistributionURL = fodDistributionURL;
        }
        String participantGLN;
        String fodEmergenceURL;
        String fodDistributionURL;
        String sdnEmergenceURL;
        String sdnDistributionURL;
    }

    // Run this method after updating how the SMP configuration is constructed. It creates the medcom-config.sh used runtime
    public static void main(String[] args) throws Exception {

        var recipients = new ArrayList<Recipient>();

        // ap1 is on FOD, ap2 is on SDN, dds on gateway (between)
        //                                        (fodEmergenceURL, fodDistributionURL, sdnEmergenceURL, sdnDistribution)
        recipients.add(new Recipient(recipientDDS, null, null, GATEWAY_HOST, null));
        recipients.add(new Recipient(recipientAP1, null, null, HOST_AP1, null));
        recipients.add(new Recipient(recipientAP2, null, null, HOST_AP2, null));

        SMPGenerator smpGenerator = new SMPGenerator(
                OUTPUT_FILE,
                "http://localhost:" + port + "/",
                recipients
        );
        smpGenerator.generate();
    }

    String outputFile;
    String smpURL;
    private List<Recipient> recipients;
    String smpSuffix;

    public SMPGenerator(String outputFile, String smpURL, List<Recipient> recipients) {
        this.outputFile = outputFile;
        this.smpURL = smpURL;
        this.recipients = recipients;
        gln2DocumentIds = new HashMap<>();
        recipients.stream().forEach(r -> {
            String[] documentIdentifiers = {
                    TEST_ACTION,
                    SBDH_ACKNOWLEDGEMENT_ACTION,
                    SBDH_EXCEPTIONACKNOWLEDGEMENT_ACTION
            };
            gln2DocumentIds.put(r.participantGLN, documentIdentifiers);
        });
        smpSuffix = participantScheme + "%3A%3A";
    }

    String participantTemplate =
            "curl --request PUT \\\n" +
            "--url ${url}#SMP_URL_SUFFIX##PARTICIPANT_GLN# \\\n" +
            "-u ${username}:${password} \\\n" +
            "--header 'content-type: application/xml' \\\n" +
            "--data '<ServiceGroup\n" +
            "xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
            "<ParticipantIdentifier scheme=\"" + participantScheme + "\">#PARTICIPANT_GLN#</ParticipantIdentifier>\n" +
            "<ServiceMetadataReferenceCollection/>\n" +
            "</ServiceGroup>'\n\n";

    private String documentIdentifierScheme(String documentId) {
        if (documentId.contains("sbdhreceiptacknowledgement")) {
            return ebxmlDocumentIdentifierScheme;
        } else {
            return medcomDocumentIdentifierScheme;
        }
    }

    String serviceTemplate(String documentId, boolean includeFODEmergence, boolean includeSDNEmergence, boolean includeFODDistribution, boolean includeSDNDistribution) {
        return "curl --request PUT \\\n" +
                "--url ${url}#SMP_URL_SUFFIX##PARTICIPANT_GLN#/services/" + documentIdentifierScheme(documentId) + "%3A%3A#ENCODED_DOCUMENT_ID# \\\n" +
                "-u ${username}:${password} \\\n" +
                "--header 'content-type: application/xml' \\\n" +
                "--data '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
                "   <ServiceInformation>\n" +
                "      <ParticipantIdentifier scheme=\"" + participantScheme + "\">#PARTICIPANT_GLN#</ParticipantIdentifier>\n" +
                "      <DocumentIdentifier scheme=\"" + documentIdentifierScheme(documentId) + "\">#DOCUMENT_ID#</DocumentIdentifier>\n" +
                "      <ProcessList>\n" +
                (includeFODEmergence ? (
                        "         <Process>\n" +
                                "            <ProcessIdentifier scheme=\"" + processIdentifierScheme + "\">" + processFODEmergence + "</ProcessIdentifier>\n" +
                                "            <ServiceEndpointList>\n" +
                                "               <Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\">\n" +
                                "                  <EndpointURI>#OPKOMST_FOD_URL#</EndpointURI>\n" +
                                "                  <Certificate>#FOD_EMERGENCE_CERT#</Certificate>\n" +
                                "                  <ServiceDescription/>\n" +
                                "                  <TechnicalContactUrl/>\n" +
                                "               </Endpoint>\n" +
                                "            </ServiceEndpointList>\n" +
                                "         </Process>\n"
                ) : "") +
                (includeSDNEmergence ? (
                        "         <Process>\n" +
                                "            <ProcessIdentifier scheme=\"" + processIdentifierScheme + "\">" + processSDNEmergence + "</ProcessIdentifier>\n" +
                                "            <ServiceEndpointList>\n" +
                                "               <Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\">\n" +
                                "                  <EndpointURI>#OPKOMST_SDN_URL#</EndpointURI>\n" +
                                "                  <Certificate>#SDN_EMERGENCE_CERT#</Certificate>\n" +
                                "                  <ServiceDescription/>\n" +
                                "                  <TechnicalContactUrl/>\n" +
                                "               </Endpoint>\n" +
                                "            </ServiceEndpointList>\n" +
                                "         </Process>\n"
                ) : "") +
                (includeFODDistribution ? (
                        "         <Process>\n" +
                                "            <ProcessIdentifier scheme=\"" + processIdentifierScheme + "\">" + processFODDistribution + "</ProcessIdentifier>\n" +
                                "            <ServiceEndpointList>\n" +
                                "               <Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\">\n" +
                                "                  <EndpointURI>#DISTRIBUTION_FOD_URL#</EndpointURI>\n" +
                                "                  <Certificate>#FOD_DISTRIBUTION_CERT#</Certificate>\n" +
                                "                  <ServiceDescription/>\n" +
                                "                  <TechnicalContactUrl/>\n" +
                                "               </Endpoint>\n" +
                                "            </ServiceEndpointList>\n" +
                                "         </Process>\n"
                ) : "") +
                (includeSDNDistribution ? (
                        "         <Process>\n" +
                                "            <ProcessIdentifier scheme=\"" + processIdentifierScheme + "\">" + processSDNDistribution + "</ProcessIdentifier>\n" +
                                "            <ServiceEndpointList>\n" +
                                "               <Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\">\n" +
                                "                  <EndpointURI>#DISTRIBUTION_SDN_URL#</EndpointURI>\n" +
                                "                  <Certificate>#SDN_DISTRIBUTION_CERT#</Certificate>\n" +
                                "                  <ServiceDescription/>\n" +
                                "                  <TechnicalContactUrl/>\n" +
                                "               </Endpoint>\n" +
                                "            </ServiceEndpointList>\n" +
                                "         </Process>\n"
                ) : "") +
                "      </ProcessList>\n" +
                "   </ServiceInformation>\n" +
                "</ServiceMetadata>'\n" +
                "\n";
    }

    public void generate() throws Exception {
        StringWriter sw = new StringWriter();

        generateHeader(sw);

        for (var recipient : recipients) {
            addParticipantEntry(sw, recipient);
        }

        for (var recipient : recipients) {
            addServiceEntry(sw, recipient);
        }

        output(sw);
    }

    private void generateHeader(StringWriter sw) {
        sw.append("#!/bin/bash -ex\n\n");
        sw.append("url=" + smpURL + "\n");
        sw.append("username=" + smpUsername + "\n");
        sw.append("password=" + smpPassword + "\n\n");
        sw.append("echo 'Waiting for SMP to become available...'\n");
        sw.append("bash -c 'while [[ \"$(curl -w %{http_code} http://localhost:" + port + ")\" != \"302\" ]]; do sleep 5; echo \"Retrying $(date)\"; done'\n");
        sw.append("echo 'SMP finished startup'\n\n");
    }

    public void addParticipantEntry(StringWriter sw, Recipient recipient) {
        String participant = participantTemplate.replace("#SMP_URL_SUFFIX#", smpSuffix);
        participant = participant.replace("#PARTICIPANT_GLN#", recipient.participantGLN);
        sw.append(participant);
    }

    public void addServiceEntry(StringWriter sw, Recipient recipient) {
        for (String documentId : gln2DocumentIds.get(recipient.participantGLN)) {
            setCertificates(recipient.participantGLN);
            boolean includeFODEmergence = recipient.fodEmergenceURL != null;
            boolean includeSDNEmergence = recipient.sdnEmergenceURL != null;
            boolean includeFODDistribution = recipient.fodDistributionURL != null;
            boolean includeSDNDistribution = recipient.sdnDistributionURL != null;

            String service = serviceTemplate(documentId, includeFODEmergence, includeSDNEmergence, includeFODDistribution, includeSDNDistribution).replaceAll("#SMP_URL_SUFFIX#", smpSuffix);
            service = replace(service, "#PARTICIPANT_GLN#", recipient.participantGLN);
            service = replace(service, "#ENCODED_DOCUMENT_ID#", encode(documentId));
            service = replace(service, "#DOCUMENT_ID#", documentId);
            service = replace(service, "#OPKOMST_FOD_URL#", recipient.fodEmergenceURL);
            service = replace(service, "#OPKOMST_SDN_URL#", recipient.sdnEmergenceURL);
            service = replace(service, "#DISTRIBUTION_FOD_URL#", recipient.fodDistributionURL);
            service = replace(service, "#DISTRIBUTION_SDN_URL#", recipient.sdnDistributionURL);

            service = replace(service, FOD_EMERGENCE_CERT, getCert(FOD_EMERGENCE_CERT, recipient.participantGLN));
            service = replace(service, SDN_EMERGENCE_CERT, getCert(SDN_EMERGENCE_CERT, recipient.participantGLN));
            service = replace(service, FOD_DISTRIBUTION_CERT, getCert(FOD_DISTRIBUTION_CERT, recipient.participantGLN));
            service = replace(service, SDN_DISTRIBUTION_CERT, getCert(SDN_DISTRIBUTION_CERT, recipient.participantGLN));
            sw.append(service);
        }
    }

    private String getCert(String type, String gln) {
        switch (gln) {
            case recipientDDS:
                return CERT_GATEWAY;
            case recipientAP1:
                return CERT_AP1;
            case recipientAP2:
                return CERT_AP2;
            default:
                return null;
        }
    }


    private String getCert_old(String type, String gln) {
        switch (gln) {
            case recipientDDS:
                if (FOD_EMERGENCE_CERT.equals(type)) return CERT_GATEWAY;
                if (SDN_EMERGENCE_CERT.equals(type)) return CERT_GATEWAY;
            case recipientAP1:
                if (FOD_EMERGENCE_CERT.equals(type)) return CERT_AP1;
                if (FOD_DISTRIBUTION_CERT.equals(type)) return CERT_AP1;
                if (SDN_EMERGENCE_CERT.equals(type)) return CERT_GATEWAY;
            case recipientAP2:
                if (FOD_EMERGENCE_CERT.equals(type)) return CERT_GATEWAY;
                if (SDN_DISTRIBUTION_CERT.equals(type)) return CERT_AP2;
                if (SDN_EMERGENCE_CERT.equals(type)) return CERT_AP2;
            default:
                return null;
        }
    }

    private String replace(String src, String pattern, String replacement) {
        if (replacement == null) {
            return src;
        } else {
            return src.replace(pattern, replacement);
        }
    }

    private void output(StringWriter sw) throws IOException {
        String contentx = sw.toString();
        System.out.println(contentx);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "utf-8"))) {
            writer.write(contentx);
        }
        System.out.println("Wrote script to file:\n" + outputFile);
    }

    private String encode(String src) {
        src = src.replaceAll("#", "%23");
        return src.replaceAll(":", "%3A");
    }
}
