package com.woowacourse.pickgit.common.factory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.http.entity.ContentType;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileFactory {

    private static final ClassLoader CLASS_LOADER = FileFactory.class.getClassLoader();
    private static final String FILE_KEY = "images";

    public static MockMultipartFile getTestImage1() {
        return createImageFile("testImage1.png");
    }

    public static MockMultipartFile getTestImage2() {
        return createImageFile("testImage2.png");
    }

    public static MockMultipartFile getEmptyTestImage() { return createEmptyImageFile();}

    public static File getTestImage1File() {
        return createFile("testImage1.png");
    }

    public static File getTestImage2File() {
        return createFile("testImage2.png");
    }

    private static MockMultipartFile createImageFile(String fileName) {
        File file = createFile(fileName);

        try {
            return new MockMultipartFile(
                FILE_KEY,
                fileName,
                ContentType.IMAGE_JPEG.getMimeType(),
                Files.readAllBytes(file.toPath())
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static MockMultipartFile createEmptyImageFile() {
        return new MockMultipartFile(
            "images",
            "",
            null,
            new byte[] {}
        );
    }

    private static File createFile(String fileName) {
        URL resource = CLASS_LOADER.getResource(fileName);
        Objects.requireNonNull(resource);

        return new File(resource.getFile());
    }
}
