package com.github.vti.amcrm.util;

public class CamelCase {
    public static String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase();
    }
}
