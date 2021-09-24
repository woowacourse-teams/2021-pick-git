package com.woowacourse.pickgit.authentication.presentation;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.infrastructure.StringEncryptor;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.authentication.presentation.dto.ReissueAccessTokenResponse;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(value = "*")
public class OAuthController {
    private static final int COOKIE_MAX_AGE = 2628000;

    private final OAuthService oauthService;

    public OAuthController(OAuthService oauthService) {
        this.oauthService = oauthService;
    }

    @GetMapping("/authorization/github")
    public ResponseEntity<OAuthLoginUrlResponse> githubAuthorizationUrl() {
        return ResponseEntity
            .ok()
            .body(new OAuthLoginUrlResponse(oauthService.getGithubAuthorizationUrl()));
    }

    @GetMapping("/afterlogin")
    public ResponseEntity<OAuthTokenResponse> afterAuthorizeGithubLogin(
        @RequestParam String code,
        HttpServletResponse response
    ) {
        TokenDto tokenDto = oauthService.createToken(code);
        response.addCookie(createRefreshTokenCookie(tokenDto.getUsername()));
        return ResponseEntity
            .ok()
            .body(new OAuthTokenResponse(tokenDto.getToken(), tokenDto.getUsername()));
    }

    @GetMapping("/token")
    public ResponseEntity<ReissueAccessTokenResponse> refreshTokenReissueAccessToken(
        @CookieValue(value = "refreshToken", required = false) Cookie cookie
    ) {
        if (Objects.isNull(cookie)) {
            throw new InvalidTokenException();
        }
        String reissuedAccessToken = oauthService.reissueAccessToken(cookie.getValue());
        return ResponseEntity.ok(new ReissueAccessTokenResponse(reissuedAccessToken));
    }

    private Cookie createRefreshTokenCookie(String username) {
        Cookie refreshCookie = new Cookie(
            "refreshToken",
            StringEncryptor.encryptToSHA256(username)
        );
        refreshCookie.setPath("/");
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(COOKIE_MAX_AGE);
        return refreshCookie;
    }
}
