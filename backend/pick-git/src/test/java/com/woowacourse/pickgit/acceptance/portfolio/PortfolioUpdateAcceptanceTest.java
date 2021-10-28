package com.woowacourse.pickgit.acceptance.portfolio;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.common.fixture.TPortfolio;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PortfolioUpdateAcceptanceTest extends AcceptanceTest {

    @DisplayName("사용자는 나의 포트폴리오를 수정한다. - 성공")
    @ParameterizedTest
    @MethodSource("getPortfolioUpdateArguments")
    void update_LoginUserWithMine_Success(
        List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests,
        List<SectionRequest> sectionRequests
    ) {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        NEOZAL.은로그인을하고().포스트를등록한다(KEVINPOST);

        PortfolioRequest 변경된_포트폴리오 = modifyPortfolio(
            contactRequests,
            projectRequests,
            sectionRequests,
            response
        );

        PortfolioResponse actual = NEOZAL.은로그인을하고().포트폴리오를_수정한다(변경된_포트폴리오)
            .as(PortfolioResponse.class);

        PortfolioResponse expected = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("사용자는 나의 포트폴리오를 수정한다. - 중복 프로젝트, 실패")
    @ParameterizedTest
    @MethodSource("getPortfolioUpdateDuplicateProjectsArguments")
    void update_LoginUserWithMineWithDuplicateProjects_Success(
        List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests,
        List<SectionRequest> sectionRequests
    ) {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        NEOZAL.은로그인을하고().포스트를등록한다(KEVINPOST);

        PortfolioRequest 변경된_포트폴리오 = modifyPortfolio(
            contactRequests,
            projectRequests,
            sectionRequests,
            response
        );

        ApiErrorResponse errorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(변경된_포트폴리오)
            .as(ApiErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo("R0004");
    }

    @DisplayName("사용자는 나의 포트폴리오를 수정한다. - 유효하지 않은 날짜 프로젝트, 실패")
    @ParameterizedTest
    @MethodSource("getPortfolioUpdateInvalidDateProjectsArguments")
    void update_LoginUserWithMineWithInvalidDateProjects_Success(
        List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests,
        List<SectionRequest> sectionRequests
    ) {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        NEOZAL.은로그인을하고().포스트를등록한다(KEVINPOST);

        PortfolioRequest 변경된_포트폴리오 = modifyPortfolio(
            contactRequests,
            projectRequests,
            sectionRequests,
            response
        );

        ApiErrorResponse errorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(변경된_포트폴리오)
            .as(ApiErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo("R0003");
    }

    @DisplayName("사용자는 나의 포트폴리오를 수정한다. - 중복 섹션, 실패")
    @ParameterizedTest
    @MethodSource("getPortfolioUpdateDuplicateSectionsArguments")
    void update_LoginUserWithMineWithDuplicateSections_Success(
        List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests,
        List<SectionRequest> sectionRequests
    ) {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        NEOZAL.은로그인을하고().포스트를등록한다(KEVINPOST);

        PortfolioRequest 변경된_포트폴리오 = modifyPortfolio(
            contactRequests,
            projectRequests,
            sectionRequests,
            response
        );

        ApiErrorResponse errorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(변경된_포트폴리오)
            .as(ApiErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo("R0005");
    }

    private PortfolioRequest modifyPortfolio(
        List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests,
        List<SectionRequest> sectionRequests,
        PortfolioResponse response
    ) {
        return new TPortfolio(response).modifier()
            .name("변경된 내용")
            .profileImageShown(false)
            .profileImageUrl("changed image url")
            .introduction("변경된 소개")
            .contacts(contactRequests)
            .projects(projectRequests)
            .sections(sectionRequests)
            .build();
    }

    @DisplayName("사용자는 남의 포트폴리오를 수정할 수 없다. - 실패")
    @Test
    void update_LoginUserWithYours_Fail() {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        PortfolioRequest 변경된_포트폴리오 = new TPortfolio(response).modifier()
            .name("변경된 내용")
            .build();

        ApiErrorResponse errorResponse = MARK.은로그인을하고().포트폴리오를_수정한다(변경된_포트폴리오)
            .as(ApiErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo("A0002");
    }

    @DisplayName("게스트는 남의 포트폴리오를 수정할 수 없다. - 실패")
    @Test
    void update_GuestUserWithYours_Fail() {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        PortfolioRequest 변경된_포트폴리오 = new TPortfolio(response).modifier()
            .name("변경된 내용")
            .build();

        ApiErrorResponse errorResponse = GUEST.는().포트폴리오를_수정한다(NEOZAL, 변경된_포트폴리오)
            .as(ApiErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo("A0001");
    }
}
