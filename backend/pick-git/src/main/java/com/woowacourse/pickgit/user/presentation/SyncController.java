package com.woowacourse.pickgit.user.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RequiredArgsConstructor
@RestController
public class SyncController {

    private final SyncService syncService;

    @GetMapping("/api/sync")
    public ResponseEntity<String> sync() {
        syncService.sync();
        return ResponseEntity.ok("ok~ done");
    }
}
