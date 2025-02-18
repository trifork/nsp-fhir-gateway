package dk.medcom.edelivery.model.mapping;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.CorrelationInformation;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class SBDHAdapter {

    private final StandardBusinessDocumentHeader sbdh;

    private SBDHAdapter(StandardBusinessDocumentHeader sbdh) {
        this.sbdh = sbdh;
    }

    public static SBDHAdapter from(StandardBusinessDocumentHeader sbdh) {
        return new SBDHAdapter(sbdh);
    }

    public Optional<String> getInstanceIdentifier(String type, String identifier) {
        return getScopes(type)
                .filter(bs -> Objects.equals(bs.getIdentifier(), identifier))
                .map(Scope::getInstanceIdentifier)
                .findAny();
    }

    public Optional<Scope> getScope(String type) {
        return getScopes(type).findAny();
    }

    public Stream<Scope> getScopes(String type) {
        if (sbdh == null) return Stream.empty();
        return getScopeStream()
                .filter(bs -> Objects.equals(bs.getType(), type));
    }

    private Stream<Scope> getScopeStream() {
        return Optional.of(sbdh)
                .map(StandardBusinessDocumentHeader::getBusinessScope)
                .map(BusinessScope::getScope)
                .stream()
                .flatMap(List::stream);
    }

    public String getRequestingDocumentIdentifier() {
        return getScopeStream()
                .flatMap(scope -> scope.getScopeInformation().stream())
                .filter(Objects::nonNull)
                .map(JAXBElement::getValue)
                .filter(CorrelationInformation.class::isInstance)
                .map(CorrelationInformation.class::cast)
                .map(CorrelationInformation::getRequestingDocumentInstanceIdentifier)
                .findFirst()
                .orElse(null);
    }
}
