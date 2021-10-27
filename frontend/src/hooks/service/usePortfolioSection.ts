import { PortfolioSection } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import { setPortfolioLocalUpdateTime } from "../../storage/storage";
import { getTemporaryId } from "../../utils/portfolio";
import useLocalStorage from "../common/useLocalStorage";

const usePortfolioSections = (username: string) => {
  const getBlackPortfolioSection = () => ({
    id: getTemporaryId(),
    name: "",
    items: [
      {
        id: getTemporaryId(10),
        category: "",
        descriptions: [
          {
            id: getTemporaryId(20),
            value: "",
          },
        ],
      },
    ],
  });

  const { itemState: portfolioSections, setItem } = useLocalStorage<PortfolioSection[]>(PORTFOLIO.SECTIONS(username), [
    getBlackPortfolioSection(),
  ]);

  const setPortfolioSections = (sections: PortfolioSection[], shouldRenewUpdateTime: boolean = true) => {
    shouldRenewUpdateTime && setPortfolioLocalUpdateTime(new Date());
    setItem(sections);
  };

  const addBlankPortfolioSection = () => {
    const newPortfolioSections = [...portfolioSections];

    setPortfolioSections([...newPortfolioSections, getBlackPortfolioSection()]);
  };

  const setPortfolioSection = (newSection: PortfolioSection) => {
    const newPortfolioSections = [...portfolioSections];
    const targetSectionIndex = newPortfolioSections.findIndex((section) => section.id === newSection.id);
    if (targetSectionIndex === -1) {
      return;
    }

    newPortfolioSections.splice(targetSectionIndex, 1, newSection);
    setPortfolioSections(newPortfolioSections);
  };

  const updatePortfolioSectionName = (prevSectionId: PortfolioSection["id"], sectionName: PortfolioSection["name"]) => {
    const newPortfolioSections = [...portfolioSections];
    const targetSection = newPortfolioSections.find((section) => section.id === prevSectionId);
    if (!targetSection) {
      return;
    }

    targetSection.name = sectionName;
    setPortfolioSections(newPortfolioSections);
  };

  const deletePortfolioSection = (sectionId: PortfolioSection["id"]) => {
    const newPortfolioSections = [...portfolioSections];
    const targetSectionIndex = newPortfolioSections.findIndex((section) => section.id === sectionId);
    if (targetSectionIndex === -1) {
      return;
    }

    newPortfolioSections.splice(targetSectionIndex, 1);
    setPortfolioSections(newPortfolioSections);
  };

  return {
    portfolioSections,
    addBlankPortfolioSection,
    setPortfolioSection,
    setPortfolioSections,
    updatePortfolioSectionName,
    deletePortfolioSection,
  };
};

export default usePortfolioSections;
