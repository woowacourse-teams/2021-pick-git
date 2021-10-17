import { PortfolioProject } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import { setPortfolioLocalUpdateTime } from "../../storage/storage";
import useLocalStorage from "./@common/useLocalStorage";

const usePortfolioProjects = () => {
  const { itemState: portfolioProjects, setItem } = useLocalStorage<PortfolioProject[]>(PORTFOLIO.PROJECTS, []);

  const setPortfolioProjects = (projects: PortfolioProject[]) => {
    setPortfolioLocalUpdateTime(new Date());
    setItem(projects);
  };

  const addPortfolioProject = (project: PortfolioProject) => {
    const newPortfolioProjects = [...portfolioProjects];
    newPortfolioProjects.push(project);

    setPortfolioProjects(newPortfolioProjects);
  };

  const updatePortfolioProject = (prevProjectName: PortfolioProject["name"], newProject: PortfolioProject) => {
    const newPortfolioProjects = [...portfolioProjects];
    const targetProjectIndex = newPortfolioProjects.findIndex((project) => project.name === prevProjectName);
    if (targetProjectIndex === -1) {
      return;
    }

    newPortfolioProjects.splice(targetProjectIndex, 1, newProject);
    setPortfolioProjects(newPortfolioProjects);
  };

  const deletePortfolioProject = (projectName: string) => {
    const newPortfolioProjects = [...portfolioProjects];
    const targetProjectIndex = newPortfolioProjects.findIndex((project) => project.name === projectName);
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
