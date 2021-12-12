package com.woowacourse.pickgit.common.file_validator;

import com.woowacourse.pickgit.exception.upload.upload.UploadFailureException;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageFileValidator implements FileValidator {
    private static final String REQUIRE_MIME_TYPE = "image";
    private static final Tika tika = new Tika();

    @Override
    public void execute(MultipartFile multipartFile) {
        try {
            String MIMEType = tika.detect(multipartFile.getInputStream());
            if(!MIMEType.startsWith(REQUIRE_MIME_TYPE)) {
                throw new UploadFailureException();
            }
        } catch (IOException e) {
            throw new UploadFailureException();
        }
    }
}
