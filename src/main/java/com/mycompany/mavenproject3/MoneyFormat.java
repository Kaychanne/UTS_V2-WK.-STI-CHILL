package com.mycompany.mavenproject3;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormat {
    public static String IDR(double value) {
        Locale idLocale = new Locale.Builder().setLanguage("in").setRegion("ID").build();
        NumberFormat idrFormat = NumberFormat.getCurrencyInstance(idLocale);
        idrFormat.setMaximumFractionDigits(2);
        return idrFormat.format(value);
    }
}
