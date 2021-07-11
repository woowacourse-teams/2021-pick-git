package com.woowacourse.pickgit.authentication.presentation;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    private OAuthService oauthService;

    public OAuthController(OAuthService oauthService) {
        this.oauthService = oauthService;
    }

    @GetMapping("/authorization/github")
    public ResponseEntity<String> githubAuthorizationUrl() {
        return ResponseEntity.ok().body(oauthService.getGithubAuthorizationUrl());
    }

    @GetMapping("/afterlogin")
    public ResponseEntity<String> afterAuthorizeGithubLogin(@RequestParam String code) {
        return ResponseEntity.ok().body(oauthService.createToken(code));
    }
}
