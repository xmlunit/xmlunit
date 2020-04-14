package org.xmlunit.assertj;

import java.util.Locale;

public class LocaleModifier {
    private Locale locale;

    public void setEnglish() {
        locale = Locale.getDefault();
        Locale.setDefault(new Locale("en"));
    }

    public void restore() {
        Locale.setDefault(locale);
    }
}
