package com.woowacourse.pickgit.portfolio.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.portfolio.application.PortfolioService;
import com.woowacourse.pickgit.portfolio.application.dto.request.UserDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.presentation.dto.PortfolioAssembler;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
@CrossOrigin(value = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @ForLoginAndGuestUser
    @GetMapping("/{username}")
    public ResponseEntity<PortfolioResponse> findPortfolioByUsername(
        @Authenticated AppUser user,
        @PathVariable String username
    ) {
        PortfolioResponseDto portfolioByUsername = portfolioService.findPortfolioByUsername(
            username,
            UserDto.from(user)
        );

        return ResponseEntity.ok(PortfolioAssembler.toPortfolioResponse(portfolioByUsername));
    }

    @ForOnlyLoginUser
    @PutMapping
    public ResponseEntity<PortfolioResponse> update(
        @Authenticated AppUser user,
        @Valid @RequestBody PortfolioRequest request
    ) {
        PortfolioResponseDto responseDto = portfolioService.update(
            PortfolioAssembler.toPortfolioRequestDto(request),
            UserDto.from(user)
        );

        return ResponseEntity.ok(PortfolioAssembler.toPortfolioResponse(responseDto));
    }
}
