import { PortfolioIntro, PortfolioProject } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import useLocalStorage from "./@common/useLocalStorage";

const usePortfolioIntro = (name?: string, description?: string) => {
  const { itemState, setItem: setPortfolioIntro } = useLocalStorage<PortfolioIntro>(PORTFOLIO.INTRO);

  const portfolioIntro = itemState ?? {
    name: "",
    isProfileShown: true,
    description: "",
    contacts: [],
  };

  const updateIntroName = (newName: string) => {
    const newPortfolioIntro = {
      ...portfolioIntro,
    };
    newPortfolioIntro.name = newName;

    setPortfolioIntro(newPortfolioIntro);
  };

  const updateIntroDescription = (newDescription: string) => {
    const newPortfolioIntro = {
      ...portfolioIntro,
    };
    newPortfolioIntro.description = newDescription;

    setPortfolioIntro(newPortfolioIntro);
  };

  const updateIsProfileShown = (isProfileShown: boolean) => {
    const newPortfolioIntro = {
      ...portfolioIntro,
    };
    newPortfolioIntro.isProfileShown = isProfileShown;

    setPortfolioIntro(newPortfolioIntro);
  };

  return {
    portfolioIntro,
    setPortfolioIntro,
    updateIntroName,
    updateIntroDescription,
    updateIsProfileShown,
  };
};

export default usePortfolioIntro;
