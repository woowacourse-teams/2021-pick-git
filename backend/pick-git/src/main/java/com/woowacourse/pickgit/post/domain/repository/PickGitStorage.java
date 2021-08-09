package com.woowacourse.pickgit.post.domain.repository;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface PickGitStorage {

    List<String> store(List<File> files, String userName);
    Optional<String> store(File file, String userName);
    File fileFrom(byte[] image);
    List<String> storeMultipartFile(List<MultipartFile> multipartFiles, String userName);
}
