import { PortfolioSection, PortfolioSectionItem } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import useLocalStorage from "./@common/useLocalStorage";

const usePortfolioSections = () => {
  const { itemState, setItem: setPortfolioSections } = useLocalStorage<PortfolioSection[]>(PORTFOLIO.SECTIONS);

  const portfolioSections = itemState ?? [
    {
      name: "",
      items: [
        {
          category: "",
          descriptions: [""],
        },
      ],
    },
  ];

  const addBlankPortfolioSection = () => {
    const newPortfolioSections = [...portfolioSections];

    setPortfolioSections([
      ...newPortfolioSections,
      {
        name: "",
        items: [
          {
            category: "",
            descriptions: [""],
          },
        ],
      },
    ]);
  };

  const setPortfolioSection = (newSection: PortfolioSection) => {
    const newPortfolioSections = [...portfolioSections];
    const targetSectionIndex = newPortfolioSections.findIndex((section) => section.name === newSection.name);
    if (targetSectionIndex === -1) {
      return;
    }

    newPortfolioSections.splice(targetSectionIndex, 1, newSection);
    setPortfolioSections(newPortfolioSections);
  };

  const updatePortfolioSectionName = (
    prevSectionName: PortfolioSection["name"],
    sectionName: PortfolioSection["name"]
  ) => {
    const newPortfolioSections = [...portfolioSections];
    const targetSection = newPortfolioSections.find((section) => section.name === prevSectionName);
    if (!targetSection) {
      return;
    }

    targetSection.name = sectionName;
    setPortfolioSections(newPortfolioSections);
  };

  const deletePortfolioSection = (sectionName: PortfolioSection["name"]) => {
    const newPortfolioSections = [...portfolioSections];
    const targetSectionIndex = newPortfolioSections.findIndex((section) => section.name === sectionName);
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
    updatePortfolioSectionName,
    deletePortfolioSection,
  };
};

export default usePortfolioSections;
