import { PortfolioSection, PortfolioSectionItem } from "../../@types";
import { PLACE_HOLDER } from "../../constants/placeholder";
import usePortfolioSectionItem from "../../hooks/service/usePortfolioSectionItem";
import PortfolioTextEditor from "../PortfolioTextEditor/PortfolioTextEditor";
import SVGIcon from "../@shared/SVGIcon/SVGIcon";
import {
  CategoriesWrapper,
  Category,
  CategoryTextareaCSS,
  CategoryAddIconWrapper,
  CategoryDeleteIconWrapper,
  SectionContentWrapper,
  Container,
  Description,
  DescriptionsWrapper,
  DescriptionItemTextareaCSS,
  DescriptionDeleteIconWrapper,
  DescriptionAddIconWrapper,
} from "./PortfolioSection.style";
import AlertPortal from "../@layout/AlertPortal/AlertPortal";
import { FAILURE_MESSAGE } from "../../constants/messages";
import { Fragment } from "react";
import useModal from "../../hooks/common/useModal";

export interface Props {
  section: PortfolioSection;
  isEditable: boolean;
  setSection?: (section: PortfolioSection) => void;
}

const PortfolioSection = ({ section, isEditable, setSection }: Props) => {
  const {
    portfolioSectionItems,
    updateCategory,
    updateDescription,
    addBlankSectionItem,
    addBlankDescription,
    deleteDescription,
    deleteSectionItem,
    isSameSectionNameExist,
  } = usePortfolioSectionItem(section, setSection);

  const {
    isModalShown: isAlertShown,
    modalMessage: alertMessage,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();

  const handleCategoryChange = (prevCategory: string) => (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    updateCategory(prevCategory, event.currentTarget.value);
  };

  const handleDescriptionChange =
    (category: string, descriptionIndex: number) => (event: React.ChangeEvent<HTMLTextAreaElement>) => {
      updateDescription(category, descriptionIndex, event.currentTarget.value);
    };

  const handleAddBlankSectionItem = () => {
    addBlankSectionItem();
  };

  const handleAddBlankDescription = (sectionItemIndex: number) => {
    addBlankDescription(sectionItemIndex);
  };

  const handleDeleteSectionItem = (sectionItemIndex: number) => {
    if (portfolioSectionItems.length === 1) {
      showAlert(FAILURE_MESSAGE.SHOULD_HAVE_LEAST_ONE_CATEGORY);
      return;
    }

    deleteSectionItem(sectionItemIndex);
  };

  const handleDeleteDescription = (
    sectionItem: PortfolioSectionItem,
    sectionItemIndex: number,
    descriptionIndex: number
  ) => {
    if (sectionItem.descriptions.length === 1) {
      showAlert(FAILURE_MESSAGE.SHOULD_HAVE_LEAST_ONE_DESCRIPTION);
      return;
    }

    deleteDescription(sectionItemIndex, descriptionIndex);
  };

  // TODO: remove index from key prop
  const categoryItems = portfolioSectionItems.map((item, sectionIndex) => (
    <SectionContentWrapper key={item.id ?? "" + sectionIndex}>
      <CategoriesWrapper>
        <Category>
          <PortfolioTextEditor
            value={item.category}
            onChange={handleCategoryChange(item.category)}
            cssProp={CategoryTextareaCSS}
            placeholder={PLACE_HOLDER.CATEGORY}
            disabled={!isEditable}
            autoGrow={true}
          />
          {isEditable && (
            <CategoryDeleteIconWrapper>
              <SVGIcon icon="DeleteCircleIcon" onClick={() => handleDeleteSectionItem(sectionIndex)} />
            </CategoryDeleteIconWrapper>
          )}
        </Category>
        {sectionIndex === portfolioSectionItems.length - 1 && isEditable && (
          <CategoryAddIconWrapper>
            <SVGIcon icon="AddCircleLargeIcon" onClick={handleAddBlankSectionItem} />
          </CategoryAddIconWrapper>
        )}
      </CategoriesWrapper>
      <DescriptionsWrapper>
        {item.descriptions.map((description, descriptionIndex) => (
          <Fragment key={description.id ?? "" + descriptionIndex}>
            <Description>
              <PortfolioTextEditor
                value={description.value}
                onChange={handleDescriptionChange(item.category, descriptionIndex)}
                cssProp={DescriptionItemTextareaCSS}
                disabled={!isEditable}
                placeholder={PLACE_HOLDER.DESCRIPTION}
                autoGrow={true}
              />
              {isEditable && (
                <DescriptionDeleteIconWrapper>
                  <SVGIcon
                    icon="DeleteCircleIcon"
                    onClick={() => handleDeleteDescription(item, sectionIndex, descriptionIndex)}
                  />
                </DescriptionDeleteIconWrapper>
              )}
            </Description>
            {descriptionIndex === item.descriptions.length - 1 && isEditable && (
              <DescriptionAddIconWrapper>
                <SVGIcon icon="AddCircleIcon" onClick={() => handleAddBlankDescription(sectionIndex)} />
              </DescriptionAddIconWrapper>
            )}
          </Fragment>
        ))}
      </DescriptionsWrapper>
    </SectionContentWrapper>
  ));

  return (
    <Container>
      {categoryItems}
      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
    </Container>
  );
};

export default PortfolioSection;
