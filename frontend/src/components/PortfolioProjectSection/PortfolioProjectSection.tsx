import {
  Container,
  ProjectNameCSS,
  ProjectPeriods,
  ProjectBody,
  ProjectInfo,
  ProjectContentCSS,
  ProjectTypeCSS,
  ProjectDateCSS,
  ProjectImage,
  TagListWrapper,
  TagItemCSS,
  ProjectDateSeparator,
} from "./PortfolioProjectSection.style";
import Chip from "../@shared/Chip/Chip";
import { PAGE_URL } from "../../constants/urls";
import PortfolioTextEditor from "../PortfolioTextEditor/PortfolioTextEditor";
import DropDown, { DropDownItem } from "../@shared/DropDown/DropDown";
import DateInput from "../@shared/DateInput/DateInput";
import usePortfolioProjectSection from "../../services/hooks/usePortfolioProject";
import { PortfolioProject } from "../../@types";

export interface Props {
  project: PortfolioProject;
  setProject: (project: PortfolioProject) => void;
}

const PortfolioProjectSection = ({ project, setProject }: Props) => {
  const { deleteTag, updateContent, updateEndDate, updateName, updateStartDate, updateType } =
    usePortfolioProjectSection(project, setProject);

  const tagList = project.tags.map((tag: string) => (
    <Chip cssProp={TagItemCSS} onDelete={() => deleteTag(tag)}>
      {tag}
    </Chip>
  ));

  const dropDownItems: DropDownItem[] = [
    {
      text: "팀 프로젝트",
      onClick: () => updateType("team"),
    },
    {
      text: "개인 프로젝트",
      onClick: () => updateType("personal"),
    },
  ];

  const handleUpdateContent: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateContent(event.currentTarget.value);
  };

  const handleUpdateStartDate: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    updateStartDate(event.currentTarget.value);
  };

  const handleUpdateEndDate: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    updateEndDate(event.currentTarget.value);
  };

  const handleUpdateName: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    updateName(event.currentTarget.value);
  };

  return (
    <Container>
      <ProjectPeriods>
        <DateInput cssProp={ProjectDateCSS} value={project.startDate} onChange={handleUpdateStartDate} />
        <ProjectDateSeparator>~</ProjectDateSeparator>
        <DateInput cssProp={ProjectDateCSS} value={project.endDate} onChange={handleUpdateEndDate} />
      </ProjectPeriods>
      <DropDown items={dropDownItems} cssProp={ProjectTypeCSS}>
        {project.type === "team" ? "팀 프로젝트" : "개인 프로젝트"}
      </DropDown>
      <PortfolioTextEditor cssProp={ProjectNameCSS} value={project.name} onChange={handleUpdateName} autoGrow />
      <ProjectBody>
        <ProjectImage src={project.imageUrl} />
        <ProjectInfo>
          <PortfolioTextEditor
            cssProp={ProjectContentCSS}
            value={project.content}
            onChange={handleUpdateContent}
            autoGrow
          />
          <TagListWrapper>{tagList}</TagListWrapper>
        </ProjectInfo>
      </ProjectBody>
    </Container>
  );
};

export default PortfolioProjectSection;
