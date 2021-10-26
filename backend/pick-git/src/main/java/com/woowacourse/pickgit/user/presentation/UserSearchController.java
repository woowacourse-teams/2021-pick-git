package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.UserAssembler;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
public class UserSearchController {

    private final UserService userService;

    @ForLoginAndGuestUser
    @GetMapping("/search/users")
    public ResponseEntity<List<UserSearchResponse>> searchUser(
        @Authenticated AppUser appUser,
        @RequestParam String keyword,
        @PageableDefault Pageable pageable
    ) {
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        List<UserSearchResponseDto> userSearchResponseDtos = userService
            .searchUser(authUserRequestDto, keyword, pageable);

        List<UserSearchResponse> userSearchResponses = UserAssembler
            .userSearchResponses(userSearchResponseDtos);

        return ResponseEntity.ok(userSearchResponses);
    }
}
