import { PortfolioProject } from "../../@types";

const usePortfolioProject = (
  portfolioProject: PortfolioProject,
  setPortfolioProject?: (portfolioProject: PortfolioProject) => void
) => {
  const updateContent = (newContent: string) => {
    const newPortfolioProject = { ...portfolioProject };
    newPortfolioProject.content = newContent;

    setPortfolioProject && setPortfolioProject(newPortfolioProject);
  };

  const updateStartDate = (newStartDate: string) => {
    const newPortfolioProject = { ...portfolioProject };
    newPortfolioProject.startDate = newStartDate;

    setPortfolioProject && setPortfolioProject(newPortfolioProject);
  };

  const updateEndDate = (newEndDate: string) => {
    const newPortfolioProject = { ...portfolioProject };
    newPortfolioProject.endDate = newEndDate;

    setPortfolioProject && setPortfolioProject(newPortfolioProject);
  };

  const updateType = (newType: "team" | "personal") => {
    const newPortfolioProject = { ...portfolioProject };
    newPortfolioProject.type = newType;

    setPortfolioProject && setPortfolioProject(newPortfolioProject);
  };

  const updateName = (newName: string) => {
    const newPortfolioProject = { ...portfolioProject };
    newPortfolioProject.name = newName;

    setPortfolioProject && setPortfolioProject(newPortfolioProject);
  };

  const deleteTag = (targetTag: string) => {
    const newPortfolioProject = { ...portfolioProject };
    const targetTagIndex = newPortfolioProject.tags.findIndex((tag) => tag === targetTag);
    if (targetTagIndex === -1) {
      return;
    }

    newPortfolioProject.tags.splice(targetTagIndex, 1);
    setPortfolioProject && setPortfolioProject(newPortfolioProject);
  };

  return {
    updateContent,
    updateStartDate,
    updateEndDate,
    updateType,
    updateName,
    deleteTag,
  };
};

export default usePortfolioProject;
