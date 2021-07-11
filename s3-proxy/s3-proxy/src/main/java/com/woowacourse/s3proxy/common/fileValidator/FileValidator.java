package com.woowacourse.s3proxy.common.fileValidator;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void execute(MultipartFile multipartFile);
}
