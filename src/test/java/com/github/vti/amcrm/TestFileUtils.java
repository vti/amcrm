package com.github.vti.amcrm;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestFileUtils {
    public static List<Path> listFilesRecursively(Path path) throws Exception {
        try (Stream<Path> walkStream = Files.walk(path)) {
            return walkStream.filter(p -> p.toFile().isFile()).collect(Collectors.toList());
        }
    }

    public static String readFileBase64(File file) throws Exception {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
