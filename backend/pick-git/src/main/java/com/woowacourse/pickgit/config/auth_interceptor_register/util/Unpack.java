package com.woowacourse.pickgit.config.auth_interceptor_register.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.SystemPropertyUtils;


public class Unpack {

    private static final int THRESHOLD_ENTRIES = 10000;
    private static final int THRESHOLD_SIZE = 1000000000;
    private static final double THRESHOLD_RATIO = 10;

    private int totalArchiveSize = 0;

    public File jar(File file) {
        try {
            return execute(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File execute(File file) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

        try (zis) {
            return writeFilesToDirectory(zis);
        }
    }

    private File writeFilesToDirectory(ZipInputStream zis) throws IOException {
        File tempDirectory = Files.createTempDirectory("pick_git").toFile();
        boolean writableOwnerOnly = tempDirectory.setWritable(true, true);
        boolean readableOwnerOnly = tempDirectory.setReadable(true, true);
        boolean executableOwnerOnly = tempDirectory.setExecutable(true, true);

        if (!(writableOwnerOnly && readableOwnerOnly && executableOwnerOnly)) {
            throw new IllegalStateException("임시 폴더를 안전하게 생성하는데 실패했습니다.");
        }

        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            int totalEntrySize = 0;

            File newFile = new File(tempDirectory, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                createDirectory(newFile);
            } else {
                createDirectoryInWindowsCase(newFile);
                totalEntrySize = writeFileToDirectory(zipEntry, zis, newFile);
            }

            if (totalArchiveSize > THRESHOLD_SIZE) {
                break;
            }

            if (totalEntrySize > THRESHOLD_ENTRIES) {
                break;
            }

            zipEntry = zis.getNextEntry();
        }

        return tempDirectory;
    }

    private void createDirectoryInWindowsCase(File newFile) {
        File parent = newFile.getParentFile();
        createDirectory(parent);
    }

    private void createDirectory(File file) {
        if (!file.isDirectory() && !file.mkdirs()) {
            throw new RuntimeException("폴더 생성에 실패했습니다" + file);
        }
    }

    private int writeFileToDirectory(
        ZipEntry zipEntry,
        ZipInputStream zis,
        File newFile
    ) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            int totalEntrySize = 0;
            int length;
            byte[] buffer = new byte[1024];

            while ((length = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                totalEntrySize += length;
                totalArchiveSize += length;

                double compressionRatio = (double) totalEntrySize / zipEntry.getCompressedSize();
                if (compressionRatio > THRESHOLD_RATIO) {
                    break;
                }
            }

            return totalEntrySize;
        }
    }

}
