package com.github.vti.amcrm.infra.photo;

import java.io.IOException;

public interface PhotoStorage {
    String store(Photo photo) throws IOException;
}
