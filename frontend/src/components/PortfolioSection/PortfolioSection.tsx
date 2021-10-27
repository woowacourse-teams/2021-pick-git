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
  } = usePortfolioSectionItem(section, setSection);

  const {
    isModalShown: isAlertShown,
    modalMessage: alertMessage,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();

  const handleCategoryChange = (sectionItemId: string | number) => (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    updateCategory(sectionItemId, event.currentTarget.value);
  };

  const handleDescriptionChange =
    (sectionItemId: string | number, descriptionId: string | number) =>
    (event: React.ChangeEvent<HTMLTextAreaElement>) => {
      updateDescription(sectionItemId, descriptionId, event.currentTarget.value);
    };

  const handleAddBlankSectionItem = () => {
    addBlankSectionItem();
  };

  const handleAddBlankDescription = (sectionItemId: string | number) => {
    addBlankDescription(sectionItemId);
  };

  const handleDeleteSectionItem = (sectionItemId: string | number) => {
    if (portfolioSectionItems.length === 1) {
      showAlert(FAILURE_MESSAGE.SHOULD_HAVE_LEAST_ONE_CATEGORY);
      return;
    }

    deleteSectionItem(sectionItemId);
  };

  const handleDeleteDescription = (sectionItem: PortfolioSectionItem, descriptionId: string | number) => {
    if (sectionItem.descriptions.length === 1) {
      showAlert(FAILURE_MESSAGE.SHOULD_HAVE_LEAST_ONE_DESCRIPTION);
      return;
    }

    deleteDescription(sectionItem.id, descriptionId);
  };

  const sectionItems = portfolioSectionItems.map((sectionItem, sectionIndex) => (
    <SectionContentWrapper key={sectionItem.id}>
      <CategoriesWrapper>
        <Category>
          <PortfolioTextEditor
            value={sectionItem.category}
            onChange={handleCategoryChange(sectionItem.id)}
            cssProp={CategoryTextareaCSS}
            placeholder={PLACE_HOLDER.CATEGORY}
            disabled={!isEditable}
            autoGrow={true}
          />
          {isEditable && (
            <CategoryDeleteIconWrapper>
              <SVGIcon icon="DeleteCircleIcon" onClick={() => handleDeleteSectionItem(sectionItem.id)} />
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
        {sectionItem.descriptions.map((description, descriptionIndex) => (
          <Fragment key={description.id}>
            <Description>
              <PortfolioTextEditor
                value={description.value}
                onChange={handleDescriptionChange(sectionItem.id, description.id)}
                cssProp={DescriptionItemTextareaCSS}
                disabled={!isEditable}
                placeholder={PLACE_HOLDER.DESCRIPTION}
                autoGrow={true}
              />
              {isEditable && (
                <DescriptionDeleteIconWrapper>
                  <SVGIcon
                    icon="DeleteCircleIcon"
                    onClick={() => handleDeleteDescription(sectionItem, description.id)}
                  />
                </DescriptionDeleteIconWrapper>
              )}
            </Description>
            {descriptionIndex === sectionItem.descriptions.length - 1 && isEditable && (
              <DescriptionAddIconWrapper>
                <SVGIcon icon="AddCircleIcon" onClick={() => handleAddBlankDescription(sectionItem.id)} />
              </DescriptionAddIconWrapper>
            )}
          </Fragment>
        ))}
      </DescriptionsWrapper>
    </SectionContentWrapper>
  ));

  return (
    <Container>
      {sectionItems}
      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
    </Container>
  );
};

export default PortfolioSection;
