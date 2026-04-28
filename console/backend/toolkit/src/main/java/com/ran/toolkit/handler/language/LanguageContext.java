package com.ran.toolkit.handler.language;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class LanguageContext {
    private LanguageContext(){
    }
    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    public static Locale getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return (locale != null) ? locale : DEFAULT_LOCALE;
    }
    public static String getLangTag() {
        return getLocale().toLanguageTag();
    }
    public static boolean isZh() {
        return "zh".equalsIgnoreCase(getLocale().getLanguage());
    }


    public static boolean isEn() {
        return "en".equalsIgnoreCase(getLocale().getLanguage());
    }
    public static void runWithLocale(Locale locale, Runnable runnable) {
        Locale prev = LocaleContextHolder.getLocale();
        try {
            LocaleContextHolder.setLocale(locale);
            runnable.run();
        } finally {
            // Restore previous context
            if (prev != null) {
                LocaleContextHolder.setLocale(prev);
            } else {
                LocaleContextHolder.resetLocaleContext();
            }
        }
    }


}
