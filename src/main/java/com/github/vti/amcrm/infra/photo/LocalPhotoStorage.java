package com.github.vti.amcrm.infra.photo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class LocalPhotoStorage implements PhotoStorage {
    private final Path rootDir;
    private final Path location;

    public LocalPhotoStorage(Path rootDir, Path location) {
        this.rootDir = rootDir;
        this.location = location;
    }

    @Override
    public String store(Photo photo) throws IOException {
        Path path =
                Paths.get(
                        rootDir.toString(),
                        location.toString(),
                        UUID.randomUUID().toString() + "-32x32.jpg");
        Files.createDirectories(path.getParent());

        // TODO: make this more efficient
        try (FileOutputStream fos = new FileOutputStream(path.toString())) {
            fos.write(photo.toByteArray());
        }

        return path.toString().replaceAll(String.format("^%s/?", rootDir), "");
    }
}
