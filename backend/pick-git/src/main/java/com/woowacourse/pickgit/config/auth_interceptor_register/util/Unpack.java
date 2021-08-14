package com.woowacourse.pickgit.config.auth_interceptor_register.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Unpack {

    public static File jar(File file) {
        try {
            return execute(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File execute(File file) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

        try (zis) {
            return writeFilesToDirectory(zis);
        }
    }

    private static File writeFilesToDirectory(ZipInputStream zis) throws IOException {
        File tempDirectory = Files.createTempDirectory("pick_git").toFile();
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = new File(tempDirectory, zipEntry.getName());

            if (zipEntry.isDirectory()) {
                createDirectory(newFile);
            } else {
                createDirectoryInWindowsCase(newFile);
                writeFileToDirectory(zis, newFile);
            }

            zipEntry = zis.getNextEntry();
        }

        return tempDirectory;
    }

    private static void createDirectoryInWindowsCase(File newFile) {
        File parent = newFile.getParentFile();
        createDirectory(parent);
    }

    private static void createDirectory(File file) {
        if (!file.isDirectory() && !file.mkdirs()) {
            throw new RuntimeException("폴더 생성에 실패했습니다" + file);
        }
    }

    private static void writeFileToDirectory(ZipInputStream zis, File newFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

}
