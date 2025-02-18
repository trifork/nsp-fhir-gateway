package dk.medcom.edelivery.integration.nas;

import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

public class PersonIdentifier {

    private String standard;

    private String value;

    @XmlElement(name = "standard", namespace = Notification.ADVIS_NAMESPACE)
    public String getStandard() {
        return standard;
    }

    public PersonIdentifier setStandard(String standard) {
        this.standard = standard;
        return this;
    }

    @XmlElement(name = "value", namespace = Notification.ADVIS_NAMESPACE)
    public String getValue() {
        return value;
    }

    public PersonIdentifier setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonIdentifier that = (PersonIdentifier) o;
        return Objects.equals(standard, that.standard) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(standard, value);
    }
}
