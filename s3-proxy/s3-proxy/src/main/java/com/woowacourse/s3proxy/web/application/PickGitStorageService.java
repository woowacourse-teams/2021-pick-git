package com.woowacourse.s3proxy.web.application;

import com.woowacourse.s3proxy.web.application.dto.FilesDto;
import com.woowacourse.s3proxy.web.domain.PickGitStorage;
import com.woowacourse.s3proxy.web.domain.PickGitStorage.StoreResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class PickGitStorageService {

    private final PickGitStorage pickGitStorage;

    public PickGitStorageService(PickGitStorage pickGitStorage) {
        this.pickGitStorage = pickGitStorage;
    }

    public FilesDto.Response store(FilesDto.Request request) {
        List<MultipartFile> files = request.getFiles();

        List<PickGitStorage.StoreResult> storeResults = pickGitStorage.store(files);

        return new FilesDto.Response(getUrlsFrom(storeResults));
    }

    private List<String> getUrlsFrom(List<PickGitStorage.StoreResult> storeResults) {
        return storeResults.stream()
            .filter(PickGitStorage.StoreResult::isSucceed)
            .map(StoreResult::getFileUrl)
            .collect(toList());
    }
}
