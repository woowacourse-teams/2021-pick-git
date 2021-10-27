import { PortfolioProject } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import { setPortfolioLocalUpdateTime } from "../../storage/storage";
import { getTemporaryId } from "../../utils/portfolio";
import useLocalStorage from "../common/useLocalStorage";

const usePortfolioProjects = (username: string) => {
  const { itemState: portfolioProjects, setItem } = useLocalStorage<PortfolioProject[]>(
    PORTFOLIO.PROJECTS(username),
    []
  );

  const setPortfolioProjects = (projects: PortfolioProject[], shouldRenewUpdateTime: boolean = true) => {
    shouldRenewUpdateTime && setPortfolioLocalUpdateTime(new Date());
    setItem(projects);
  };

  const addPortfolioProject = (project: PortfolioProject) => {
    const newPortfolioProjects = [...portfolioProjects];
    newPortfolioProjects.push({
      ...project,
      id: getTemporaryId(),
    });

    setPortfolioProjects(newPortfolioProjects);
  };

  const updatePortfolioProject = (prevProjectId: PortfolioProject["id"], newProject: PortfolioProject) => {
    const newPortfolioProjects = [...portfolioProjects];
    const targetProjectIndex = newPortfolioProjects.findIndex((project) => project.id === prevProjectId);
    if (targetProjectIndex === -1) {
      return;
    }

    newPortfolioProjects.splice(targetProjectIndex, 1, newProject);
    setPortfolioProjects(newPortfolioProjects);
  };

  const deletePortfolioProject = (prevProjectId: PortfolioProject["id"]) => {
    const newPortfolioProjects = [...portfolioProjects];
    const targetProjectIndex = newPortfolioProjects.findIndex((project) => project.id === prevProjectId);
    if (targetProjectIndex === -1) {
      return;
    }

    newPortfolioProjects.splice(targetProjectIndex, 1);

    setPortfolioProjects(newPortfolioProjects);
  };

  return {
    portfolioProjects,
    addPortfolioProject,
    deletePortfolioProject,
    updatePortfolioProject,
    setPortfolioProjects,
  };
};

export default usePortfolioProjects;
