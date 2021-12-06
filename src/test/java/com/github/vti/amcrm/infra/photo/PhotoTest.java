package com.github.vti.amcrm.infra.photo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;

public class PhotoTest {

    @Test
    void resizesImage() throws Exception {
        String base64 = TestData.getPhotoFileBase64();
        Photo photo = Photo.load(base64);

        Photo resizedPhoto = photo.resize(32, 32);

        assertNotNull(resizedPhoto);
    }
}
