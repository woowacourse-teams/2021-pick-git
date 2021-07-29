package com.woowacourse.s3_proxy.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.tika.Tika;
import org.springframework.mock.web.MockMultipartFile;

public class FileFactory {

    private static final Tika tika = new Tika();
    private static final ClassLoader classLoader = FileFactory.class.getClassLoader();

    public static MockMultipartFile getTestFailData() {
        return createMockMultipartFile("testFailData.sh");
    }

    public static MockMultipartFile getTestFailImage1() {
        return createMockMultipartFile("testFailImage1.jpg");
    }

    public static MockMultipartFile getTestFailImage2() {
        return createMockMultipartFile("testFailImage2.jpg");
    }

    public static MockMultipartFile getTestRightImage1() {
        return createMockMultipartFile("testRightImage1.png");
    }

    public static MockMultipartFile getTestRightImage2() {
        return createMockMultipartFile("testRightImage2.png");
    }

    public static File getTestFailDataFile() {
        return createFile("testFailData.sh");
    }

    public static File getTestFailImage1File() {
        return createFile("testFailImage1.jpg");
    }

    public static File getTestFailImage2File() {
        return createFile("testFailImage2.jpg");
    }

    public static File getTestRightImage1File() {
        return createFile("testRightImage1.png");
    }

    public static File getTestRightImage2File() {
        return createFile("testRightImage2.png");
    }

    private static File createFile(String fileName) {
        URL resource = classLoader.getResource(fileName);
        Objects.requireNonNull(resource);

        return new File(resource.getFile());
    }

    private static MockMultipartFile createMockMultipartFile(String fileName) {
        File file = createFile(fileName);

        try {
            return new MockMultipartFile(
                "files",
                fileName,
                tika.detect(file),
                Files.readAllBytes(file.toPath())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
