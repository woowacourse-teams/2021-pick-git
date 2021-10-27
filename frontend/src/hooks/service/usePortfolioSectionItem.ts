import { PortfolioSection } from "../../@types";
import { getTemporaryId } from "../../utils/portfolio";

const usePortfolioSectionItem = (
  portfolioSection: PortfolioSection,
  setPortfolioSection?: (portfolioSection: PortfolioSection) => void
) => {
  const getBlankSectionItem = () => ({
    id: getTemporaryId(),
    category: "",
    descriptions: [
      {
        id: getTemporaryId(10),
        value: "",
      },
    ],
  });

  const addBlankSectionItem = () => {
    const newPortfolioSection = { ...portfolioSection };
    newPortfolioSection.items.push(getBlankSectionItem());

    setPortfolioSection && setPortfolioSection(newPortfolioSection);
  };

  const addBlankDescription = (sectionItemId: number | string) => {
    const newPortfolioSection = { ...portfolioSection };
    const targetSectionItem = newPortfolioSection.items.find((item) => item.id === sectionItemId);

    if (!targetSectionItem) {
      return;
    }

    targetSectionItem.descriptions.push({ id: getTemporaryId(), value: "" });
    setPortfolioSection && setPortfolioSection(newPortfolioSection);
  };

  const deleteSectionItem = (sectionItemId: number | string) => {
    const newPortfolioSection = { ...portfolioSection };
    const targetSectionItemIndex = newPortfolioSection.items.findIndex((item) => item.id === sectionItemId);

    if (targetSectionItemIndex === -1) {
      return;
    }

    newPortfolioSection.items.splice(targetSectionItemIndex, 1);
    setPortfolioSection && setPortfolioSection(newPortfolioSection);
  };

  const updateCategory = (sectionItemId: number | string, currentCategory: string) => {
    const newPortfolioSection = { ...portfolioSection };

    const targetSectionItem = newPortfolioSection.items.find((sectionItem) => sectionItem.id === sectionItemId);
    if (!targetSectionItem) {
      return;
    }

    targetSectionItem.category = currentCategory;
    setPortfolioSection && setPortfolioSection(newPortfolioSection);
  };

  const updateDescription = (
    sectionItemId: number | string,
    descriptionId: number | string,
    newDescription: string
  ) => {
    const newPortfolioSection = { ...portfolioSection };

    const targetSectionItem = newPortfolioSection.items.find((sectionItem) => sectionItem.id === sectionItemId);
    if (!targetSectionItem) {
      return;
    }

    const targetDescription = targetSectionItem.descriptions.find((description) => description.id === descriptionId);

    if (!targetDescription) {
      return;
    }

    targetDescription.value = newDescription;
    setPortfolioSection && setPortfolioSection(newPortfolioSection);
  };

  const deleteDescription = (sectionItemId: number | string, descriptionId: number | string) => {
    const newPortfolioSection = { ...portfolioSection };

    const targetSectionItem = newPortfolioSection.items.find((sectionItem) => sectionItem.id === sectionItemId);
    if (!targetSectionItem) {
      return;
    }

    const targetDescriptionIndex = targetSectionItem.descriptions.findIndex(
      (description) => description.id === descriptionId
    );

    if (targetDescriptionIndex === -1) {
      return;
    }

    targetSectionItem.descriptions.splice(targetDescriptionIndex, 1);
    setPortfolioSection && setPortfolioSection(newPortfolioSection);
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
