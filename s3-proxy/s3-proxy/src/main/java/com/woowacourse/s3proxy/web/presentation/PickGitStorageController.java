package com.woowacourse.s3proxy.web.presentation;

import com.woowacourse.s3proxy.web.application.PickGitStorageService;
import com.woowacourse.s3proxy.web.application.dto.FilesDto;
import com.woowacourse.s3proxy.web.presentation.Dto.Files;
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
    public ResponseEntity<Files.Response> store(Files.Request files) {
        FilesDto.Request requestDto = new FilesDto.Request(files.getUserName(), files.getFiles());
        FilesDto.Response responseDto = pickGitStorageService.store(requestDto);

        return ResponseEntity.ok(
            new Files.Response(responseDto.getUrls())
        );
    }
}
