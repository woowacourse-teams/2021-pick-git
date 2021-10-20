package com.woowacourse.s3_proxy.web.infrastructure;

import com.woowacourse.s3_proxy.exception.format.FileExtensionException;
import com.woowacourse.s3_proxy.exception.format.HashFailureException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import org.apache.commons.codec.binary.Hex;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileNameGenerator {

    private static final Tika tika = new Tika();

    public String generate(MultipartFile multipartFile, String userName) {
        return md5(multipartFile, userName) + extension(multipartFile);
    }

    private String md5(MultipartFile multipartFile, String userName) {
        try {
            final String fileName = multipartFile.getOriginalFilename();

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update((fileName + userName + LocalDateTime.now())
                .getBytes(StandardCharsets.UTF_8));

            return Hex.encodeHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new HashFailureException("파일 이름 해시화 실패");
        }
    }

    private String extension(MultipartFile multipartFile) {
        MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
        try {
            MimeType mimeType = defaultMimeTypes.forName(
                tika.detect(multipartFile.getBytes())
            );
            return mimeType.getExtension();
        } catch (MimeTypeException | IOException e) {
            throw new FileExtensionException("확장자 추출 실패");
        }
    }
}
