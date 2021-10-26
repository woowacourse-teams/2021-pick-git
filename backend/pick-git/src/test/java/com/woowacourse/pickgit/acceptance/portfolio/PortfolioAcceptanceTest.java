package com.woowacourse.pickgit.acceptance.portfolio;

import static com.woowacourse.pickgit.query.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.query.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.query.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.query.fixture.TUser.MARK;
import static com.woowacourse.pickgit.query.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import com.woowacourse.pickgit.query.fixture.TContact;
import com.woowacourse.pickgit.query.fixture.TPortfolio;
import com.woowacourse.pickgit.query.fixture.TProject;
import com.woowacourse.pickgit.query.fixture.TSection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PortfolioAcceptanceTest extends AcceptanceTest {

    @DisplayName("사용자는 나의 포트폴리오를 수정한다 성공")
    @ParameterizedTest
    @MethodSource("getParametersForPortfolioUpdate")
    void update_LoginUserWithMineWithNewAllAndSingleSize_Success(
        List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests,
        List<SectionRequest> sectionRequests
    ) {
        PortfolioResponse response = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        NEOZAL.은로그인을하고().포스트를등록한다(KEVINPOST);

        PortfolioRequest 변경된_포트폴리오 =
            modifyPortfolio(contactRequests, projectRequests, sectionRequests, response);

        PortfolioResponse modifiedPortfolioResponse =
            NEOZAL.은로그인을하고().포트폴리오를_수정한다(변경된_포트폴리오).as(PortfolioResponse.class);

        PortfolioRequest actual = new TPortfolio(modifiedPortfolioResponse)
            .modifier()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(
                ".*id",
                ".*createdAt",
                ".*updatedAt",
                ".*startDate",
                ".*endDate"
            )
            .ignoringCollectionOrder()
            .isEqualTo(변경된_포트폴리오);
    }

    private PortfolioRequest modifyPortfolio(List<ContactRequest> contactRequests,
        List<ProjectRequest> projectRequests, List<SectionRequest> sectionRequests,
        PortfolioResponse response) {
        PortfolioRequest 변경된_포트폴리오 = new TPortfolio(response).modifier()
            .name("변경된 내용")
            .profileImageShown(false)
            .profileImageUrl("changed image url")
            .introduction("변경된 소개")
            .contacts(contactRequests)
            .projects(projectRequests)
            .sections(sectionRequests)
            .build();
        return 변경된_포트폴리오;
    }

    @DisplayName("사용자는 남의 포트폴리오를 수정할 수 없다 - 실패")
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

    @DisplayName("게스트는 남의 포트폴리오를 수정할 수 없다 - 실패")
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
