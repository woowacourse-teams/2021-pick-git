package com.woowacourse.s3proxy.common.filevalidator;

import com.woowacourse.s3proxy.exception.UploadFailException;
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
                throw new UploadFailException();
            }
        } catch (IOException e) {
            throw new UploadFailException();
        }
    }
}
