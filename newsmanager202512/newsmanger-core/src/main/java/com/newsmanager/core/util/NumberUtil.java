package com.newsmanager.core.util;

public class NumberUtil {

    static public String getrandom() {
        String items = String.valueOf((Math.random()));
        return items.substring(2, 6);
    }
}