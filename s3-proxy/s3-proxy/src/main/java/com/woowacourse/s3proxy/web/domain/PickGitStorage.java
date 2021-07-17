package com.woowacourse.s3proxy.web.domain;

import java.util.List;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public interface PickGitStorage {

    List<StoreResult> store(List<MultipartFile> multipartFiles, String userName);

    class StoreResult {

        private final String originalFileName;
        private final String fileUrl;

        public StoreResult(String originalFileName, String fileUrl) {
            this.originalFileName = originalFileName;
            this.fileUrl = fileUrl;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }
    }
}
