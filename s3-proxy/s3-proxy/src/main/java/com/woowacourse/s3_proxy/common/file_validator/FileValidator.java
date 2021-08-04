package com.woowacourse.s3_proxy.common.file_validator;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void execute(MultipartFile multipartFile);
}
