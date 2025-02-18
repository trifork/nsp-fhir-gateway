package dk.medcom.edelivery.integration.domibus.model;

import org.springframework.util.StringUtils;

import java.util.Locale;

public class Description {
    private String value;
    private Locale lang;

    public Description(Locale lang, String description) {
        if (!StringUtils.hasLength(description)) {
            throw new IllegalArgumentException("description must not be empty");
        }

        Locale tLang = lang;
        if (tLang == null) {
            tLang = Locale.getDefault();
        }
        this.lang = tLang;
        this.value = description;
    }

    public Locale getLang() {
        return lang;
    }

    public String getValue() {
        return value;
    }
}
