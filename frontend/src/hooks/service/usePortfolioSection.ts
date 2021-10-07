import { PortfolioSection } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import { setPortfolioLocalUpdateTime } from "../../storage/storage";
import useLocalStorage from "../common/useLocalStorage";

const usePortfolioSections = () => {
  const { itemState: portfolioSections, setItem } = useLocalStorage<PortfolioSection[]>(PORTFOLIO.SECTIONS, [
    {
      id: null,
      name: "",
      items: [
        {
          id: null,
          category: "",
          descriptions: [
            {
              id: null,
              value: "",
            },
          ],
        },
      ],
    },
  ]);

  const setPortfolioSections = (sections: PortfolioSection[]) => {
    setPortfolioLocalUpdateTime(new Date());
    setItem(sections);
  };

  const addBlankPortfolioSection = () => {
    const newPortfolioSections = [...portfolioSections];

    setPortfolioSections([
      ...newPortfolioSections,
      {
        id: null,
        name: "",
        items: [
          {
            id: null,
            category: "",
            descriptions: [
              {
                id: null,
                value: "",
              },
            ],
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
    setPortfolioSections,
    updatePortfolioSectionName,
    deletePortfolioSection,
  };
};

export default usePortfolioSections;
