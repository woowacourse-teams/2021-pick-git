package com.woowacourse.s3_proxy.common.file_validator;

import com.woowacourse.s3_proxy.exception.upload.UploadFailureException;
import java.io.IOException;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

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
