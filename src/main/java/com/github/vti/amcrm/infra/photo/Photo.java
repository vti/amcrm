package com.github.vti.amcrm.infra.photo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

public class Photo {
    private final BufferedImage image;

    public Photo(BufferedImage image) {
        Objects.requireNonNull(image);

        this.image = image;
    }

    public static Photo load(String base64) {
        Objects.requireNonNull(base64);

        Base64.Decoder decoder = Base64.getDecoder();

        byte[] bytes = decoder.decode(base64);

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            BufferedImage image = ImageIO.read(bis);

            if (image == null) {
                throw new RuntimeException("Image loading failed");
            }

            return new Photo(image);
        } catch (Exception e) {
            throw new RuntimeException("Image loading failed", e);
        }
    }

    public Photo resize(int targetWidth, int targetHeight) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(image)
                .size(targetWidth, targetHeight)
                .outputFormat("JPEG")
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return new Photo(ImageIO.read(inputStream));
    }

    public byte[] toByteArray() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "JPEG", baos);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
