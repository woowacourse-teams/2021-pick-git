package com.woowacourse.s3proxy.common.filevalidator;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void execute(MultipartFile multipartFile);
}
