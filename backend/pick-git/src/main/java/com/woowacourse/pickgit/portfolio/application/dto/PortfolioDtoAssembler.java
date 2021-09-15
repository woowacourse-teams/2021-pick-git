package com.woowacourse.pickgit.portfolio.application.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.portfolio.TagNotFoundException;
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
import com.woowacourse.pickgit.portfolio.application.dto.response.TagResponseDto;
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

    public Portfolio of(PortfolioRequestDto portfolioRequestDto) {
        return new Portfolio(
            portfolioRequestDto.getId(),
            portfolioRequestDto.isProfileImageShown(),
            portfolioRequestDto.getProfileImageUrl(),
            portfolioRequestDto.getIntroduction(),
            toContacts(portfolioRequestDto.getContacts()),
            toProjects(portfolioRequestDto.getProjects()),
            toSections(portfolioRequestDto.getSections())
        );
    }

    private Contacts toContacts(List<ContactRequestDto> contactRequestDtos) {
        List<Contact> contacts = contactRequestDtos.stream()
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

    private Projects toProjects(List<ProjectRequestDto> projectRequestDtos) {
        List<Project> projects = projectRequestDtos.stream()
            .map(this::toProject)
            .collect(toList());

        return new Projects(projects);
    }

    private Project toProject(ProjectRequestDto projectRequestDto) {
        List<ProjectTag> tags = projectRequestDto.getTags().stream()
            .map(this::toTag)
            .collect(toList());

        return new Project(
            projectRequestDto.getId(),
            projectRequestDto.getName(),
            projectRequestDto.getStartDate(),
            projectRequestDto.getEndDate(),
            projectRequestDto.getType(),
            projectRequestDto.getImageUrl(),
            projectRequestDto.getContent(),
            tags
        );
    }

    private ProjectTag toTag(TagRequestDto tagRequestDto) {
        Tag tag = tagRepository.findById(tagRequestDto.getId())
            .orElseThrow(TagNotFoundException::new);

        return new ProjectTag(
            tagRequestDto.getId(),
            tag
        );
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

    public PortfolioResponseDto of(Portfolio portfolio) {
        return new PortfolioResponseDto(
            portfolio.getId(),
            portfolio.isProfileImageShown(),
            portfolio.getProfileImageUrl(),
            portfolio.getIntroduction(),
            of(portfolio.getContacts()),
            of(portfolio.getProjects()),
            of(portfolio.getSections())
        );
    }

    private List<ContactResponseDto> of(Contacts contacts) {
        return contacts.getContacts().stream()
            .map(this::of)
            .collect(toList());
    }

    private ContactResponseDto of(Contact contact) {
        return new ContactResponseDto(
            contact.getId(),
            contact.getCategory(),
            contact.getValue()
        );
    }

    private List<ProjectResponseDto> of(Projects projects) {
        return projects.getValue().stream()
            .map(this::of)
            .collect(toList());
    }

    private ProjectResponseDto of(Project project) {
        List<TagResponseDto> tags = project.getTags().stream()
            .map(this::of)
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

    private TagResponseDto of(ProjectTag tag) {
        return new TagResponseDto(
            tag.getId(),
            tag.getName()
        );
    }

    private List<SectionResponseDto> of(Sections sections) {
        return sections.getValues().stream()
            .map(this::of)
            .collect(toList());
    }

    private SectionResponseDto of(Section section) {
        List<ItemResponseDto> items = section.getItems().stream()
            .map(this::of)
            .collect(toList());

        return new SectionResponseDto(
            section.getId(),
            section.getName(),
            items
        );
    }

    private ItemResponseDto of(Item item) {
        List<DescriptionResponseDto> descriptions = item.getDescriptions().stream()
            .map(this::of)
            .collect(toList());

        return new ItemResponseDto(
            item.getId(),
            item.getCategory(),
            descriptions
        );
    }

    private DescriptionResponseDto of(Description description) {
        return new DescriptionResponseDto(
            description.getId(),
            description.getValue()
        );
    }
}
