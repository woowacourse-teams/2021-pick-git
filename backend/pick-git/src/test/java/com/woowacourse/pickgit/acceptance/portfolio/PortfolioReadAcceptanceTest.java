package com.woowacourse.pickgit.acceptance.portfolio;

import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KODA;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.portfolio.application.dto.PortfolioDtoAssembler;
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class PortfolioReadAcceptanceTest extends AcceptanceTest {

    @Autowired
    private PortfolioDtoAssembler portfolioDtoAssembler;

    @BeforeEach
    void setUp() {
        toRead();
        KODA.은로그인을한다();
        MARK.은로그인을하고().포트폴리오를_조회한다(MARK);
        NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL);
    }

    @DisplayName("사용자는 포트폴리오를 '최초' 조회한다 나의 포트폴리오 - 성공")
    @Test
    void read_LoginUserWithMine_Success() {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt", "updatedAt")
            .isEqualTo(portfolioDtoAssembler.toPortfolioResponseDto(Portfolio.empty(NEOZAL.toEntity())));
    }

    @DisplayName("사용자는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 존재하는 경우 - 성공")
    @Test
    void read_LoginUserWithYours_IfExisting_Success() {
        MARK.은로그인을하고().포트폴리오를_조회한다(MARK);
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(MARK)
            .as(PortfolioResponse.class);

        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt", "updatedAt")
            .isEqualTo(portfolioDtoAssembler.toPortfolioResponseDto(Portfolio.empty(MARK.toEntity())));
    }

    @DisplayName("사용자는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 없는 경우 - 실패")
    @Test
    void read_LoginUserWithYours_IfNotExisting_Fail() {
        ExtractableResponse<Response> response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(KODA);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("게스트는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 존재하는 경우 - 성공")
    @Test
    void read_GuestUserWithExistingYours_Success() {

        PortfolioResponse response = GUEST.는().포트폴리오를_조회한다(MARK).as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt", "updatedAt")
            .isEqualTo(portfolioDtoAssembler.toPortfolioResponseDto(Portfolio.empty(MARK.toEntity())));
    }

    @DisplayName("게스트는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 존재하지 않는 경우 - 실패")
    @Test
    void read_GuestUserWithNonExistingYours_Fail() {
        ExtractableResponse<Response> response = GUEST.는().포트폴리오를_조회한다(KODA);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
