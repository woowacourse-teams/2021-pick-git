package com.woowacourse.pickgit.post.domain.repository;

import java.io.File;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PickGitStorage {

    List<String> store(List<File> files, String userName);
    List<String> storeMultipartFile(List<MultipartFile> multipartFiles, String userName);
}
