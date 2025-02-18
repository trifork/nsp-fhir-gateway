package dk.medcom.edelivery.integration.domibus.model;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class TypedProperty {
    private String key;
    private String value;
    private String type;

    public TypedProperty(String key, String value) {
        this(key, value, null);
    }

    public TypedProperty(String key, String value, String type) {
        if (!StringUtils.hasLength(key) || !StringUtils.hasLength(value)) {
            throw new IllegalArgumentException("message properties must have a non-empty name and value (key [" + key + "], value [" + value + "]");
        }
        this.key = key;
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypedProperty)) return false;
        TypedProperty that = (TypedProperty) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, type);
    }

    @Override
    public String toString() {
        return "TypedProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
