package com.woowacourse.s3_proxy.web.presentation;

import com.woowacourse.s3_proxy.web.application.PickGitStorageService;
import com.woowacourse.s3_proxy.web.application.dto.FilesDto;
import com.woowacourse.s3_proxy.web.presentation.dto.Files;
import com.woowacourse.s3_proxy.web.presentation.resolver.ExtensionValid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/storage")
public class PickGitStorageController {

    private final PickGitStorageService pickGitStorageService;

    public PickGitStorageController(PickGitStorageService pickGitStorageService) {
        this.pickGitStorageService = pickGitStorageService;
    }

    @PostMapping
    public ResponseEntity<Files.Response> store(@ExtensionValid Files.Request files) {
        FilesDto.Request requestDto = new FilesDto.Request(files.getUserName(), files.getFiles());
        FilesDto.Response responseDto = pickGitStorageService.store(requestDto);

        return ResponseEntity.ok(
            new Files.Response(responseDto.getUrls())
        );
    }
}
