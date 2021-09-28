package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
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

    public static PortfolioRequest mockPortfolioRequestWithNewAllAndSingleSize() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .contacts(List.of(mockContactRequest(1L)))
            .projects(List.of(mockProjectRequest(1L)))
            .sections(List.of(mockSectionRequest(1L)))
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewAllAndMultipleSize() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .contacts(List.of(mockContactRequest(1L), mockContactRequest(2L)))
            .projects(List.of(mockProjectRequest(1L), mockProjectRequest(2L)))
            .sections(List.of(mockSectionRequest(1L), mockSectionRequest(2L)))
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewProfileAndIntroduction() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image2")
            .introduction("hello~")
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
            .contacts(List.of(mockContactRequest(1L), mockContactRequest(2L)))
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
            .contacts(List.of())
            .projects(List.of(mockProjectRequest(1L), mockProjectRequest(2L)))
            .sections(List.of())
            .build();
    }

    public static PortfolioRequest mockPortfolioRequestWithNewSection() {
        return PortfolioRequest.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of(mockSectionRequest(1L), mockSectionRequest(2L)))
            .build();
    }

    private static ContactRequest mockContactRequest(Long id) {
        return ContactRequest.builder()
            .id(id)
            .category("phone")
            .value("010-0000-0000")
            .build();
    }

    private static ProjectRequest mockProjectRequest(Long id) {
        return ProjectRequest.builder()
            .id(id)
            .name("pickgit")
            .startDate(LocalDateTime.MIN)
            .endDate(LocalDateTime.MAX)
            .type("team")
            .imageUrl("image")
            .content("pickgit")
            .tags(List.of(mockTagRequest()))
            .build();
    }

    private static TagRequest mockTagRequest() {
        return TagRequest.builder()
            .id(1L)
            .name("java")
            .build();
    }

    private static SectionRequest mockSectionRequest(Long id) {
        return SectionRequest.builder()
            .id(id)
            .name("experience")
            .items(List.of(mockItemRequest(id)))
            .build();
    }

    private static ItemRequest mockItemRequest(Long id) {
        return ItemRequest.builder()
            .id(id)
            .category("woowacourse")
            .descriptions(List.of(mockDescriptionRequest(id)))
            .build();
    }

    private static DescriptionRequest mockDescriptionRequest(Long id) {
        return DescriptionRequest.builder()
            .id(id)
            .value("3rd backend")
            .build();
    }

    public static PortfolioResponse mockPortfolioResponseWithNewAllAndSingleSize() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .contacts(List.of(mockContactResponse(1L)))
            .projects(List.of(mockProjectResponse(1L)))
            .sections(List.of(mockSectionResponse(1L)))
            .build();
    }

    public static PortfolioResponse mockPortfolioResponseWithNewAllAndMultipleSize() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(false)
            .profileImageUrl("image2")
            .introduction("hello~")
            .contacts(List.of(mockContactResponse(1L), mockContactResponse(2L)))
            .projects(List.of(mockProjectResponse(1L), mockProjectResponse(2L)))
            .sections(List.of(mockSectionResponse(1L), mockSectionResponse(2L)))
            .build();
    }

    public static PortfolioResponse mockPortfolioResponseWithNewProfileAndIntroduction() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image2")
            .introduction("hello~")
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioResponse mockPortfolioResponseWithNewContact() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .contacts(List.of(mockContactResponse(1L), mockContactResponse(2L)))
            .projects(List.of())
            .sections(List.of())
            .build();
    }

    public static PortfolioResponse mockPortfolioResponseWithNewProject() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .contacts(List.of())
            .projects(List.of(mockProjectResponse(1L), mockProjectResponse(2L)))
            .sections(List.of())
            .build();
    }

    public static PortfolioResponse mockPortfolioResponseWithNewSection() {
        return PortfolioResponse.builder()
            .id(1L)
            .profileImageShown(true)
            .profileImageUrl("image")
            .introduction("hi~")
            .contacts(List.of())
            .projects(List.of())
            .sections(List.of(mockSectionResponse(1L), mockSectionResponse(2L)))
            .build();
    }

    private static ContactResponse mockContactResponse(Long id) {
        return ContactResponse.builder()
            .id(id)
            .category("phone")
            .value("010-0000-0000")
            .build();
    }

    private static ProjectResponse mockProjectResponse(Long id) {
        return ProjectResponse.builder()
            .id(id)
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

    private static SectionResponse mockSectionResponse(Long id) {
        return SectionResponse.builder()
            .id(id)
            .name("experience")
            .items(List.of(mockItemResponse(id)))
            .build();
    }

    private static ItemResponse mockItemResponse(Long id) {
        return ItemResponse.builder()
            .id(id)
            .category("woowacourse")
            .descriptions(List.of(mockDescriptionResponse(id)))
            .build();
    }

    private static DescriptionResponse mockDescriptionResponse(Long id) {
        return DescriptionResponse.builder()
            .id(id)
            .value("3rd backend")
            .build();
    }
}
