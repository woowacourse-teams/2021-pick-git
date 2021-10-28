package com.woowacourse.pickgit.acceptance.portfolio;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.common.fixture.CPost;
import com.woowacourse.pickgit.common.fixture.TItem;
import com.woowacourse.pickgit.common.fixture.TPortfolio;
import com.woowacourse.pickgit.common.fixture.TPost;
import com.woowacourse.pickgit.common.fixture.TProject;
import com.woowacourse.pickgit.common.fixture.TSection;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PortfolioConstraintsAcceptanceTest extends AcceptanceTest {

    @DisplayName("포트폴리오의 유저이름은 50자를 넘을 수 없다.")
    @Test
    void portfolio_username_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        String name = "a".repeat(51);
        PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .name(name)
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 자기소개는 200자를 넘을 수 없다.")
    @Test
    void portfolio_introduction_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        String introduction = "a".repeat(201);
        PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .introduction(introduction)
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 프로젝트 내용은 3000자를 넘을 수 없다.")
    @Test
    void portfolio_project_content_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        String content = "a".repeat(3001);
        CPost sourcePost = CPost.builder()
            .content(content)
            .build();

        PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .projects(List.of(TProject.of(TPost.of(sourcePost))))
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 프로젝트 개수는 10개를 넘을 수 없다")
    @Test
    void portfolio_project_size_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        final PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .projects(List.of(
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST),
                TProject.of(NEOZALPOST)
            ))
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 섹션 개수는 10개를 넘을 수 없다")
    @Test
    void portfolio_section_size_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        final PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .sections(List.of(
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom(),
                TSection.createRandom()
            ))
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 섹션의 카테고리는 50자를 넘을 수 없다.")
    @Test
    void portfolio_section_category_length_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        String category = "a".repeat(51);
        SectionRequest sectionRequest = new SectionRequest(
            null,
            "",
            List.of(
                new ItemRequest(
                    null,
                    category,
                    List.of()
                )
            )
        );

        final PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .sections(List.of(sectionRequest))
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 섹션의 설명글은 300자를 넘을 수 없다.")
    @Test
    void portfolio_section_description_length_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        String value = "a".repeat(301);
        SectionRequest sectionRequest = new SectionRequest(
            null,
            "",
            List.of(
                new ItemRequest(
                    null,
                    "",
                    List.of(new DescriptionRequest(
                        null,
                        value
                    ))
                )
            )
        );

        final PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .sections(List.of(sectionRequest))
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }

    @DisplayName("포트폴리오의 섹션의 카테고리 수는 10개를 넘을 수 없다.")
    @Test
    void portfolio_section_category_size_constraint() {
        PortfolioResponse portfolioResponse = NEOZAL.은로그인을하고().포트폴리오를_조회한다(NEOZAL)
            .as(PortfolioResponse.class);

        SectionRequest sectionRequest = new SectionRequest(
            null,
            "",
            List.of(
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom(),
                TItem.createRandom()
            )
        );

        final PortfolioRequest portfolioRequest = new TPortfolio(portfolioResponse).modifier()
            .sections(List.of(sectionRequest))
            .build();

        ApiErrorResponse apiErrorResponse = NEOZAL.은로그인을하고().포트폴리오를_수정한다(portfolioRequest)
            .as(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode()).isEqualTo("R0006");
    }
}
