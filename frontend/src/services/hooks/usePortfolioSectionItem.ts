import { PortfolioSection } from "../../@types";

const usePortfolioSectionItem = (
  portfolioSection: PortfolioSection,
  setPortfolioSection: (portfolioSection: PortfolioSection) => void
) => {
  const addBlankSectionItem = () => {
    const newPortfolioSection = { ...portfolioSection };
    newPortfolioSection.items.push({
      id: null,
      category: "",
      descriptions: [
        {
          id: null,
          value: "",
        },
      ],
    });

    setPortfolioSection(newPortfolioSection);
  };

  const addBlankDescription = (sectionItemIndex: number) => {
    const newPortfolioSection = { ...portfolioSection };
    newPortfolioSection.items[sectionItemIndex].descriptions.push({ id: null, value: "" });
    setPortfolioSection(newPortfolioSection);
  };

  const deleteSectionItem = (sectionItemIndex: number) => {
    const newPortfolioSection = { ...portfolioSection };
    newPortfolioSection.items.splice(sectionItemIndex, 1);

    setPortfolioSection(newPortfolioSection);
  };

  const updateCategory = (prevCategory: string, currentCategory: string) => {
    const newPortfolioSection = { ...portfolioSection };

    const targetSectionItem = newPortfolioSection.items.find((sectionItem) => sectionItem.category === prevCategory);
    if (!targetSectionItem) {
      return;
    }

    targetSectionItem.category = currentCategory;
    setPortfolioSection(newPortfolioSection);
  };

  const updateDescription = (category: string, descriptionIndex: number, newDescription: string) => {
    const newPortfolioSection = { ...portfolioSection };

    const targetSectionItem = newPortfolioSection.items.find((sectionItem) => sectionItem.category === category);
    if (!targetSectionItem) {
      return;
    }

    targetSectionItem.descriptions[descriptionIndex].value = newDescription;
    setPortfolioSection(newPortfolioSection);
  };

  const deleteDescription = (sectionItemIndex: number, descriptionIndex: number) => {
    const newPortfolioSection = { ...portfolioSection };
    newPortfolioSection.items[sectionItemIndex].descriptions.splice(descriptionIndex, 1);
    setPortfolioSection(newPortfolioSection);
  };

  const isSameSectionNameExist = (sectionName?: string) => {
    const sectionNames = portfolioSection.items.map((sectionItem) => sectionItem.category);
    if (!sectionName) {
      return sectionNames.length !== new Set(sectionNames).size;
    }

    const newSectionNames = [...sectionNames, sectionName];
    return newSectionNames.length !== new Set(newSectionNames).size;
  };

  return {
    portfolioSectionItems: portfolioSection.items,
    addBlankSectionItem,
    addBlankDescription,
    deleteSectionItem,
    updateCategory,
    updateDescription,
    deleteDescription,
    isSameSectionNameExist,
  };
};

export default usePortfolioSectionItem;
