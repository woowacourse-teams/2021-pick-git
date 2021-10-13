package com.woowacourse.pickgit.common.factory;

import static java.time.Month.OCTOBER;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioResponse mockPortfolioResponse(String name) {
        return PortfolioResponse.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("https://github.com/testImage.jpg")
            .introduction("testDescription")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewAllAndSingleSize(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of(mockContactRequest()))
            .projects(List.of(mockProjectRequest()))
            .sections(List.of(mockSectionRequest()))
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewAllAndMultipleSize(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of(mockContactRequest(), mockContactRequest()))
            .projects(List.of(mockProjectRequest(), mockProjectRequest()))
            .sections(List.of(mockSectionRequest(), mockSectionRequest()))
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewProfileAndIntroduction(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("image2")
            .introduction("hello~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewContact(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of(mockContactRequest(), mockContactRequest()))
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewProject(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .contacts(List.of())
            .projects(List.of(mockProjectRequest(), mockProjectRequest()))
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewSection(String name) {
        return PortfolioRequest.builder()
            .id(1L)
            .name(name)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .createdAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
            .updatedAt(LocalDateTime.of(2021, OCTOBER, 1, 13, 10))
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
