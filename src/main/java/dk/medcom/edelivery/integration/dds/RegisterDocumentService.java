package dk.medcom.edelivery.integration.dds;

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


import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.lcm.SubmitObjectsRequest;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryResponseType;
import org.openehealth.ipf.commons.ihe.xds.iti42.Iti42PortType;

import java.util.stream.Collectors;

public class RegisterDocumentService {

    private final Iti42PortType iti42PortType;

    public RegisterDocumentService(Iti42PortType iti42PortType) {
        this.iti42PortType = iti42PortType;
    }

    public String registerDocument(SubmitObjectsRequest request) {
        RegistryResponseType response = iti42PortType.documentRegistryRegisterDocumentSetB(request);
        checkResponse(response);
        return response.getStatus();
    }

    private void checkResponse(RegistryResponseType response) {
        if (hasErrors(response)) {
            String errorMessage = response.getRegistryErrorList().getRegistryError().stream()
                    .map(error -> error.getErrorCode() + "/" + error.getCodeContext())
                    .collect(Collectors.joining(",\n"));
            throw new RegisterDocumentError("An error occurred updating DDS registry: " + errorMessage);
        }
    }

    private boolean hasErrors(RegistryResponseType response) {
        return response.getRegistryErrorList() != null
                && response.getRegistryErrorList().getRegistryError() != null
                && !response.getRegistryErrorList().getRegistryError().isEmpty();
    }

}
