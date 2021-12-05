package com.github.vti.amcrm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestData {
    public static String getRandomId() {
        return UUID.randomUUID().toString();
    }

    public static String getRandomName() {
        return getRandomFromList(Arrays.asList("John", "Bill", "Mary"));
    }

    public static String getRandomSurname() {
        return getRandomFromList(Arrays.asList("Doe", "Bush", "Smith"));
    }

    public static File getPhotoFile() {
        return new File("src/test/resources/duck.jpg");
    }

    public static String getPhotoFileBase64() throws Exception {
        return TestFileUtils.readFileBase64(getPhotoFile());
    }

    public static File getConfigFile() {
        return new File("src/test/resources/configs/valid.yml");
    }

    public static <T> T getRandomFromList(List<T> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}
