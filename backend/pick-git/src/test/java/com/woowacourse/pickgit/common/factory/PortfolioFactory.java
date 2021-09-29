package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioResponse mockPortfolioResponse() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewAllAndSingleSize() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of(mockContactRequest()))
            .projects(List.of(mockProjectRequest()))
            .sections(List.of(mockSectionRequest()))
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewAllAndMultipleSize() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of(mockContactRequest(), mockContactRequest()))
            .projects(List.of(mockProjectRequest(), mockProjectRequest()))
            .sections(List.of(mockSectionRequest(), mockSectionRequest()))
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewProfileAndIntroduction() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewContact() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of(mockContactRequest(), mockContactRequest()))
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewProject() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of())
            .projects(List.of(mockProjectRequest(), mockProjectRequest()))
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewSection() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(null)
            .updatedAt(null)
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of(mockSectionRequest(), mockSectionRequest()))
            .build();
    }

    private static ContactRequest mockContactRequest() {
        return ContactRequest.builder()
            .id(null)
            .category("phone")
            .value("010-0000-0000")
            .build();
    }

    private static ProjectRequest mockProjectRequest() {
        return ProjectRequest.builder()
            .id(null)
            .name("pickgit")
            .startDate(null)
            .endDate(null)
            .type("team")
            .imageUrl("image")
            .content("pickgit")
            .tags(List.of(mockTagRequest(1L, "java"), mockTagRequest(2L, "spring")))
            .build();
    }

    private static TagRequest mockTagRequest(Long id, String name) {
        return TagRequest.builder()
            .id(id)
            .name(name)
            .build();
    }

    private static SectionRequest mockSectionRequest() {
        return SectionRequest.builder()
            .id(null)
            .name("experience")
            .items(List.of(mockItemRequest()))
            .build();
    }

    private static ItemRequest mockItemRequest() {
        return ItemRequest.builder()
            .id(null)
            .category("woowacourse")
            .descriptions(List.of(mockDescriptionRequest()))
            .build();
    }

    private static DescriptionRequest mockDescriptionRequest() {
        return DescriptionRequest.builder()
            .id(null)
            .value("3rd backend")
            .build();
    }
}
