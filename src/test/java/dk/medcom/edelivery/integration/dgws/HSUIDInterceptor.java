/*
 * The MIT License
 *
 * Original work sponsored and donated by The Danish Health Data Authority (http://www.sundhedsdatastyrelsen.dk)
 *
 * Copyright (C) 2018 The Danish Health Data Authority (http://www.sundhedsdatastyrelsen.dk)
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.medcom.edelivery.integration.dgws;

import dk.nsi.hsuid.*;
import dk.nsi.hsuid._2016._08.hsuid_1_1.HsuidHeader;
import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class HSUIDInterceptor extends AbstractSoapInterceptor {

    private final DGWSHeaderFactory headerFactory;

    public HSUIDInterceptor(DGWSHeaderFactory headerFactory) {
        super(Phase.PRE_STREAM);
        this.headerFactory = headerFactory;
    }

    @Override
    public void handleMessage(SoapMessage message) {
        addHsuidHeader(message);
    }

    private void addHsuidHeader(SoapMessage message) {
        String cpr = "0506504003";
        try {

            List<Attribute> attributes = new ArrayList<>();
            attributes.add(new CitizenCivilRegistrationNumberAttribute(cpr)); // ActingUserCivilRegistrationNumber
            attributes.add(UserTypeAttribute.HEALTHCAREPROFESSIONAL); // UserType
            attributes.add(new ActingUserCivilRegistrationNumberAttribute("0506704003")); // ActingUserCivilRegistrationNumber
            attributes.add(new OrganisationIdentifierAttribute("293591000016003", OrganisationIdentifierAttribute.LegalFormatNames.SOR)); // ActingUserCivilRegistrationNumber
            attributes.add(new SystemVendorNameAttribute("TRIFORK A/S")); // SystemOwnerName
            attributes.add(new SystemNameAttribute("CDAViewer")); // SystemName
            attributes.add(new SystemVersionAttribute("1.0")); // SystemVersion
            attributes.add(new OperationsOrganisationNameAttribute("TRIFORK A/S")); // OrgResponsibleName
            attributes.add(new ConsentOverrideAttribute(true)); // ActingUserCivilRegistrationNumber
            attributes.add(new ResponsibleUserCivilRegistrationNumberAttribute("0506704003")); // ActingUserCivilRegistrationNumber
            attributes.add(new ResponsibleUserAuthorizationCodeAttribute("FYT03")); // ActingUserCivilRegistrationNumber

            HsuidHeader hsuidHeader =
                    HealthcareServiceUserIdentificationHeaderUtil.createHealthcareServiceUserIdentificationHeader("TRUST2408", attributes);

            JAXBContext jc = JAXBContext.newInstance(HsuidHeader.class);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            jc.createMarshaller().marshal(hsuidHeader, document);

            Node element = document.getDocumentElement();
            message.getHeaders().add(new Header(new QName(element.getNamespaceURI(), element.getLocalName()), element));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
