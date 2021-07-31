package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(value = "*")
public class UserSearchController {

    private UserService userService;

    public UserSearchController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search/users")
    public ResponseEntity<List<UserSearchResponseDto>> searchUser(
        @Authenticated AppUser appUser,
        @RequestParam String keyword,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
        UserSearchRequestDto userSearchRequestDto = UserSearchRequestDto.builder()
            .keyword(keyword)
            .page(page)
            .limit(limit)
            .build();
        return ResponseEntity
            .ok(userService.searchUser(authUserRequestDto, userSearchRequestDto));
    }
}
