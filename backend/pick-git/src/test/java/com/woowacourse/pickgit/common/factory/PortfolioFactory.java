package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.portfolio.presentation.dto.response.ContactResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.DescriptionResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ItemResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ProjectResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.SectionResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.TagResponse;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioResponse mockPortfolioResponse() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    private static ContactResponse mockContactResponse() {
        return ContactResponse.builder()
            .id(1L)
            .category("phone")
            .value("010-0000-0000")
            .build();
    }

    private static ProjectResponse mockProjectResponse() {
        return ProjectResponse.builder()
            .id(1L)
            .name("pickgit")
            .startDate(LocalDateTime.MIN)
            .endDate(LocalDateTime.MAX)
            .type("team")
            .imageUrl("image")
            .content("pickgit")
            .tags(List.of(mockTagResponse()))
            .build();
    }

    private static TagResponse mockTagResponse() {
        return TagResponse.builder()
            .id(1L)
            .name("java")
            .build();
    }

    private static SectionResponse mockSectionResponse() {
        return SectionResponse.builder()
            .id(1L)
            .name("experience")
            .items(List.of(mockItemResponse()))
            .build();
    }

    private static ItemResponse mockItemResponse() {
        return ItemResponse.builder()
            .id(1L)
            .category("woowacourse")
            .descriptions(List.of(mockDescriptionResponse()))
            .build();
    }

    private static DescriptionResponse mockDescriptionResponse() {
        return DescriptionResponse.builder()
            .id(1L)
            .value("3rd backend")
            .build();
    }
}
