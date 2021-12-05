package com.github.vti.amcrm.infra.photo;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFileUtils;

class LocalPhotoStorageTest {

    private LocalPhotoStorage storage;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() {
        storage = new LocalPhotoStorage(tmpDir, Paths.get("customer"));
    }

    @Test
    void store() throws Exception {
        String base64 = TestData.getPhotoFileBase64();
        Photo photo = Photo.load(base64);

        String location = storage.store(photo);

        List<Path> files = TestFileUtils.listFilesRecursively(tmpDir);

        assertEquals(1, files.size());
        assertTrue(location.matches("customer/.*-32x32\\.jpg"));
        assertEquals(Paths.get(location).toFile().getName(), files.get(0).toFile().getName());
    }
}
