package com.woowacourse.pickgit.authentication.presentation;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class OAuthController {

    private final OAuthService oauthService;

    @GetMapping("/authorization/github")
    public ResponseEntity<OAuthLoginUrlResponse> githubAuthorizationUrl() {
        return ResponseEntity
            .ok()
            .body(new OAuthLoginUrlResponse(oauthService.getGithubAuthorizationUrl()));
    }

    @GetMapping("/afterlogin")
    public ResponseEntity<OAuthTokenResponse> afterAuthorizeGithubLogin(@RequestParam String code) {
        TokenDto tokenDto = oauthService.createToken(code);
        return ResponseEntity
            .ok()
            .body(new OAuthTokenResponse(tokenDto.getToken(), tokenDto.getUsername()));
    }
}
