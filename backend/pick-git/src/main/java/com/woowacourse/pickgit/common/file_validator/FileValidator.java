package com.woowacourse.pickgit.common.file_validator;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void execute(MultipartFile multipartFile);
}
