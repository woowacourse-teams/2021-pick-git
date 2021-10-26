package com.woowacourse.pickgit.portfolio.application.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.application.dto.request.ContactRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.DescriptionRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ItemRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.ProjectRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.SectionRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.TagRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ContactResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.DescriptionResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ItemResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.PortfolioResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.ProjectResponseDto;
import com.woowacourse.pickgit.portfolio.application.dto.response.SectionResponseDto;
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.contact.Contact;
import com.woowacourse.pickgit.portfolio.domain.contact.Contacts;
import com.woowacourse.pickgit.portfolio.domain.project.Project;
import com.woowacourse.pickgit.portfolio.domain.project.ProjectTag;
import com.woowacourse.pickgit.portfolio.domain.project.Projects;
import com.woowacourse.pickgit.portfolio.domain.section.Section;
import com.woowacourse.pickgit.portfolio.domain.section.Sections;
import com.woowacourse.pickgit.portfolio.domain.section.item.Description;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PortfolioDtoAssembler {

    private final TagRepository tagRepository;

    public PortfolioDtoAssembler(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Portfolio toPortfolio(PortfolioRequestDto portfolioRequestDto) {
        return new Portfolio(
            portfolioRequestDto.getId(),
            portfolioRequestDto.getName(),
            portfolioRequestDto.isProfileImageShown(),
            portfolioRequestDto.getProfileImageUrl(),
            portfolioRequestDto.getIntroduction(),
            portfolioRequestDto.getCreatedAt(),
            portfolioRequestDto.getUpdatedAt(),
            toContacts(portfolioRequestDto.getContacts()),
            toProjects(portfolioRequestDto.getProjects()),
            toSections(portfolioRequestDto.getSections())
        );
    }

    private Contacts toContacts(List<ContactRequestDto> contactRequestsDto) {
        List<Contact> contacts = contactRequestsDto.stream()
            .map(this::toContact)
            .collect(toList());

        return new Contacts(contacts);
    }

    private Contact toContact(ContactRequestDto contactRequestDto) {
        return new Contact(
            contactRequestDto.getId(),
            contactRequestDto.getCategory(),
            contactRequestDto.getValue()
        );
    }

    private Projects toProjects(List<ProjectRequestDto> projectRequestsDto) {
        List<Project> projects = projectRequestsDto.stream()
            .map(this::toProject)
            .collect(toList());

        return new Projects(projects);
    }

    private Project toProject(ProjectRequestDto projectRequestDto) {
        List<String> tagNames = projectRequestDto.getTags().stream()
            .map(TagRequestDto::getName)
            .collect(toList());
        List<ProjectTag> projectTags = toProjectTags(tagNames);

        return new Project(
            projectRequestDto.getId(),
            projectRequestDto.getName(),
            projectRequestDto.getStartDate(),
            projectRequestDto.getEndDate(),
            projectRequestDto.getType(),
            projectRequestDto.getImageUrl(),
            projectRequestDto.getContent(),
            projectTags
        );
    }

    private List<ProjectTag> toProjectTags(List<String> tagNames) {
        List<Tag> tags = tagRepository.findTagByNameIn(tagNames);

        return tags.stream()
            .map(ProjectTag::new)
            .collect(toList());
    }

    private Sections toSections(List<SectionRequestDto> sectionRequestDtos) {
        List<Section> sections = sectionRequestDtos.stream()
            .map(this::toSection)
            .collect(toList());

        return new Sections(sections);
    }

    private Section toSection(SectionRequestDto sectionRequestDto) {
        List<Item> items = sectionRequestDto.getItems().stream()
            .map(this::toItem)
            .collect(toList());

        return new Section(
            sectionRequestDto.getId(),
            sectionRequestDto.getName(),
            items
        );
    }

    private Item toItem(ItemRequestDto itemRequestDto) {
        List<Description> descriptions = itemRequestDto.getDescriptions().stream()
            .map(this::toDescription)
            .collect(toList());

        return new Item(
            itemRequestDto.getId(),
            itemRequestDto.getCategory(),
            descriptions
        );
    }

    private Description toDescription(DescriptionRequestDto descriptionRequestDto) {
        return new Description(
            descriptionRequestDto.getId(),
            descriptionRequestDto.getValue()
        );
    }

    public PortfolioResponseDto toPortfolioResponseDto(Portfolio portfolio) {
        return new PortfolioResponseDto(
            portfolio.getId(),
            portfolio.getName(),
            portfolio.isProfileImageShown(),
            portfolio.getProfileImageUrl(),
            portfolio.getIntroduction(),
            portfolio.getCreatedAt(),
            portfolio.getUpdatedAt(),
            toContactResponsesDto(portfolio.getContacts()),
            toProjectResponsesDto(portfolio.getProjects()),
            toSectionResponsesDto(portfolio.getSections())
        );
    }

    private List<ContactResponseDto> toContactResponsesDto(Contacts contacts) {
        return contacts.getValues().stream()
            .map(this::toContactResponseDto)
            .collect(toList());
    }

    private ContactResponseDto toContactResponseDto(Contact contact) {
        return new ContactResponseDto(
            contact.getId(),
            contact.getCategory(),
            contact.getValue()
        );
    }

    private List<ProjectResponseDto> toProjectResponsesDto(Projects projects) {
        return projects.getValues().stream()
            .map(this::toProjectResponseDto)
            .collect(toList());
    }

    private ProjectResponseDto toProjectResponseDto(Project project) {
        List<String> tags = project.getTags().stream()
            .map(ProjectTag::getTagName)
            .collect(toList());

        return new ProjectResponseDto(
            project.getId(),
            project.getName(),
            project.getStartDate(),
            project.getEndDate(),
            project.getType().getValue(),
            project.getImageUrl(),
            project.getContent(),
            tags
        );
    }

    private List<SectionResponseDto> toSectionResponsesDto(Sections sections) {
        return sections.getValues().stream()
            .map(this::toSectionResponseDto)
            .collect(toList());
    }

    private SectionResponseDto toSectionResponseDto(Section section) {
        List<ItemResponseDto> items = section.getItems().stream()
            .map(this::toItemResponseDto)
            .collect(toList());

        return new SectionResponseDto(
            section.getId(),
            section.getName(),
            items
        );
    }

    private ItemResponseDto toItemResponseDto(Item item) {
        List<DescriptionResponseDto> descriptions = item.getDescriptions().stream()
            .map(this::toDescriptionResponseDto)
            .collect(toList());

        return new ItemResponseDto(
            item.getId(),
            item.getCategory(),
            descriptions
        );
    }

    private DescriptionResponseDto toDescriptionResponseDto(Description description) {
        return new DescriptionResponseDto(
            description.getId(),
            description.getValue()
        );
    }
}
