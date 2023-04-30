package com.marcosviniciusferreira.casaflow.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codeBase64(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT)
                .replaceAll("(\\n|\\r)", "");


    }

    public static String decodeBase64(String text) {
        return new String(Base64.decode(text, Base64.DEFAULT));
    }
}
