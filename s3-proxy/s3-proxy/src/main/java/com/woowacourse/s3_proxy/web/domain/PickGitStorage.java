package com.woowacourse.s3_proxy.web.domain;

import java.util.List;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public interface PickGitStorage {

    List<StoreResult> store(List<MultipartFile> multipartFiles, String userName);

    class StoreResult {

        private final String originalFileName;
        private final String fileUrl;
        private final Exception exception;

        public StoreResult(String originalFileName, String fileUrl) {
            this(originalFileName, fileUrl, null);
        }

        public StoreResult(String originalFileName, Exception exception) {
            this(originalFileName, null, exception);
        }

        private StoreResult(String originalFileName, String fileUrl, Exception exception) {
            this.originalFileName = originalFileName;
            this.fileUrl = fileUrl;
            this.exception = exception;
        }

        public boolean isSucceed() {
            return !Objects.isNull(fileUrl);
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getFileUrl() {
            if (!isSucceed()) {
                throw new UnsupportedOperationException();
            }
            return fileUrl;
        }

        public Exception getException() {
            if (isSucceed()) {
                throw new UnsupportedOperationException();
            }
            return this.exception;
        }
    }
}
