package com.woowacourse.pickgit.authentication.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.url.redirect-url")
    private String redirectUrl;

    @GetMapping("/authorization/github")
    public ResponseEntity<String> loginPageUrl() {
        String githubLoginUrl = "https://github.com/login/oauth/authorize?"
            + "client_id=" + clientId
            + "&redirect_url=" + redirectUrl;

        return ResponseEntity.ok().body(githubLoginUrl);
    }
}
