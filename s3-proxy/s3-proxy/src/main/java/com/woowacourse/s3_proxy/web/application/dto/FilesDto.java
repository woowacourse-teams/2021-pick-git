package com.woowacourse.s3_proxy.web.application.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FilesDto {

    class Request {

        private String userName;
        private List<MultipartFile> files;

        private Request() {
        }

        public Request(String userName, List<MultipartFile> files) {
            this.userName = userName;
            this.files = files;
        }

        public String getUserName() {
            return userName;
        }

        public List<MultipartFile> getFiles() {
            return files;
        }
    }

    class Response {

        private List<String> urls;

        private Response() {
        }

        public Response(List<String> urls) {
            this.urls = urls;
        }

        public List<String> getUrls() {
            return urls;
        }
    }
}
