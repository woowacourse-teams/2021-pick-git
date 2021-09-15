package com.woowacourse.pickgit.portfolio.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.portfolio.NoSuchPortfolioException;
import com.woowacourse.pickgit.exception.post.NoSuchTagException;
import com.woowacourse.pickgit.portfolio.application.dto.request.AuthUserForPortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ContactRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.DescriptionRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ItemRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ProjectRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.SectionRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.TagRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ContactResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ProjectResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.SectionResponseDto;
import com.woowacourse.pickgit.portfolio.domain.Contact;
import com.woowacourse.pickgit.portfolio.domain.Description;
import com.woowacourse.pickgit.portfolio.domain.Item;
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.PortfolioRepository;
import com.woowacourse.pickgit.portfolio.domain.Project;
import com.woowacourse.pickgit.portfolio.domain.ProjectTag;
import com.woowacourse.pickgit.portfolio.domain.Section;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TagRepository tagRepository;

    public PortfolioService(
        PortfolioRepository portfolioRepository,
        TagRepository tagRepository
    ) {
        this.portfolioRepository = portfolioRepository;
        this.tagRepository = tagRepository;
    }

    public PortfolioResponseDto update(
        AuthUserForPortfolioRequestDto authUserRequestDto,
        PortfolioRequestDto portfolioRequestDto
    ) {
        validateIsGuest(authUserRequestDto);

        Portfolio portfolio = portfolioRepository.findById(portfolioRequestDto.getId())
            .orElseThrow(NoSuchPortfolioException::new);

        boolean profileImageShown = portfolio
            .updateProfileImageShown(portfolioRequestDto.isProfileImageShown());
        String profileImageUrl = portfolio
            .updateProfileImageUrl(portfolioRequestDto.getProfileImageUrl());
        String introduction = portfolio
            .updateIntroduction(portfolioRequestDto.getIntroduction());
        List<Contact> contacts = portfolio
            .updateContacts(createContacts(portfolioRequestDto.getContacts(), portfolio));
        List<Project> projects = portfolio
            .updateProjects(createProjects(portfolioRequestDto.getProjects(), portfolio));
        List<Section> sections = portfolio
            .updateSections(createSections(portfolioRequestDto.getSections(), portfolio));

        return PortfolioResponseDto.from(
            portfolio.getId(),
            profileImageShown,
            profileImageUrl,
            introduction,
            getContactResponsesDto(contacts),
            getProjectResponsesDto(projects),
            getSectionResponsesDto(sections)
        );
    }

    private void validateIsGuest(AuthUserForPortfolioRequestDto authUserRequestDto) {
        if (authUserRequestDto.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    private List<Contact> createContacts(List<ContactRequestDto> requestsDto, Portfolio portfolio) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactRequestDto requestDto : requestsDto) {
            contacts.add(createContact(requestDto, portfolio));
        }
        return contacts;
    }

    private Contact createContact(ContactRequestDto requestDto, Portfolio portfolio) {
        return new Contact(
            requestDto.getId(),
            requestDto.getCategory(),
            requestDto.getValue(),
            portfolio
        );
    }

    private List<Project> createProjects(List<ProjectRequestDto> requestsDto, Portfolio portfolio) {
        List<Project> projects = new ArrayList<>();
        for (ProjectRequestDto requestDto : requestsDto) {
            projects.add(createProject(requestDto, portfolio));
        }
        return projects;
    }

    private Project createProject(ProjectRequestDto requestDto, Portfolio portfolio) {
        return new Project(
            requestDto.getId(),
            requestDto.getName(),
            requestDto.getStartDate(),
            requestDto.getEndDate(),
            requestDto.getType(),
            requestDto.getImageUrl(),
            requestDto.getContent(),
            getTags(requestDto.getTags()),
            portfolio
        );
    }

    private List<ProjectTag> getTags(List<TagRequestDto> sourceTags) {
        List<ProjectTag> tags = new ArrayList<>();
        for (TagRequestDto source : sourceTags) {
            tags.add(new ProjectTag(source.getId(), null, findTagByName(source.getName())));
        }
        return tags;
    }

    private Tag findTagByName(String tag) {
        return tagRepository.findByName(tag)
            .orElseThrow(NoSuchTagException::new);
    }

    private List<Section> createSections(List<SectionRequestDto> requestsDto, Portfolio portfolio) {
        List<Section> sections = new ArrayList<>();
        for (SectionRequestDto requestDto : requestsDto) {
            sections.add(createSection(requestDto, portfolio));
        }
        return sections;
    }

    private Section createSection(SectionRequestDto requestDto, Portfolio portfolio) {
        return new Section(
            requestDto.getId(),
            requestDto.getName(),
            getItems(requestDto.getItems()),
            portfolio
        );
    }

    private List<Item> getItems(List<ItemRequestDto> sourceItems) {
        List<Item> items = new ArrayList<>();
        for (ItemRequestDto sourceItem : sourceItems) {
            items.add(new Item(
                sourceItem.getId(),
                sourceItem.getCategory(),
                getDescriptions(sourceItem.getDescriptions()),
                null)
            );
        }
        return items;
    }

    private List<Description> getDescriptions(List<DescriptionRequestDto> sourceDescriptions) {
        List<Description> descriptions = new ArrayList<>();
        for (DescriptionRequestDto sourceDescription : sourceDescriptions) {
            descriptions.add(new Description(
                sourceDescription.getId(),
                sourceDescription.getValue(),
                null)
            );
        }
        return descriptions;
    }

    private List<ContactResponseDto> getContactResponsesDto(List<Contact> contacts) {
        return contacts.stream()
            .map(ContactResponseDto::of)
            .collect(toList());
    }

    private List<ProjectResponseDto> getProjectResponsesDto(List<Project> projects) {
        return projects.stream()
            .map(ProjectResponseDto::of)
            .collect(toList());
    }

    private List<SectionResponseDto> getSectionResponsesDto(List<Section> sections) {
        return sections.stream()
            .map(SectionResponseDto::of)
            .collect(toList());
    }
}
