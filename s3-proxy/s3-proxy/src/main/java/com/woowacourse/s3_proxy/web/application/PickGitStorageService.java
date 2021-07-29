package com.woowacourse.s3_proxy.web.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.s3_proxy.web.application.dto.FilesDto;
import com.woowacourse.s3_proxy.web.domain.PickGitStorage;
import com.woowacourse.s3_proxy.web.domain.PickGitStorage.StoreResult;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PickGitStorageService {

    private final PickGitStorage pickGitStorage;

    public PickGitStorageService(PickGitStorage pickGitStorage) {
        this.pickGitStorage = pickGitStorage;
    }

    public FilesDto.Response store(FilesDto.Request request) {
        List<MultipartFile> files = request.getFiles();
        String userName = request.getUserName();

        List<PickGitStorage.StoreResult> storeResults = pickGitStorage.store(files, userName);

        return new FilesDto.Response(getUrlsFrom(storeResults));
    }

    private List<String> getUrlsFrom(List<PickGitStorage.StoreResult> storeResults) {
        return storeResults.stream()
            .map(StoreResult::getFileUrl)
            .collect(toList());
    }
}
