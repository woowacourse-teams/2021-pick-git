import { StringDecoder } from "string_decoder";
import { PortfolioSection, PortfolioSectionItem } from "../../@types";
import { PLACE_HOLDER } from "../../constants/placeholder";
import usePortfolioSectionItem from "../../services/hooks/usePortfolioSectionItem";
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
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import { FAILURE_MESSAGE } from "../../constants/messages";

export interface Props {
  section: PortfolioSection;
  setSection: (section: PortfolioSection) => void;
}

const PortfolioSection = ({ section, setSection }: Props) => {
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

  const { modalMessage, isModalShown, showAlertModal, hideMessageModal } = useMessageModal();

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
    if (sectionItemIndex === 0) {
      showAlertModal(FAILURE_MESSAGE.SHOULD_HAVE_LEAST_ONE_CATEGORY);
      return;
    }

    deleteSectionItem(sectionItemIndex);
  };

  const handleDeleteDescription = (sectionItemIndex: number, descriptionIndex: number) => {
    if (descriptionIndex === 0) {
      showAlertModal(FAILURE_MESSAGE.SHOULD_HAVE_LEAST_ONE_DESCRIPTION);
      return;
    }

    deleteDescription(sectionItemIndex, descriptionIndex);
  };

  const categoryItems = portfolioSectionItems.map((item, sectionIndex) => (
    <SectionContentWrapper>
      <CategoriesWrapper>
        <Category>
          <PortfolioTextEditor
            value={item.category}
            onChange={handleCategoryChange(item.category)}
            cssProp={CategoryTextareaCSS}
            placeholder={PLACE_HOLDER.CATEGORY}
            autoGrow={true}
          />
          <CategoryDeleteIconWrapper>
            <SVGIcon icon="DeleteCircleIcon" onClick={() => handleDeleteSectionItem(sectionIndex)} />
          </CategoryDeleteIconWrapper>
        </Category>
        {sectionIndex === portfolioSectionItems.length - 1 && (
          <CategoryAddIconWrapper>
            <SVGIcon icon="AddCircleLargeIcon" onClick={handleAddBlankSectionItem} />
          </CategoryAddIconWrapper>
        )}
      </CategoriesWrapper>
      <DescriptionsWrapper>
        {item.descriptions.map((description, descriptionIndex) => (
          <>
            <Description>
              <PortfolioTextEditor
                value={description}
                onChange={handleDescriptionChange(item.category, descriptionIndex)}
                cssProp={DescriptionItemTextareaCSS}
                autoGrow={true}
                placeholder={PLACE_HOLDER.DESCRIPTION}
              />
              <DescriptionDeleteIconWrapper>
                <SVGIcon
                  icon="DeleteCircleIcon"
                  onClick={() => handleDeleteDescription(sectionIndex, descriptionIndex)}
                />
              </DescriptionDeleteIconWrapper>
            </Description>
            {descriptionIndex === item.descriptions.length - 1 && (
              <DescriptionAddIconWrapper>
                <SVGIcon icon="AddCircleIcon" onClick={() => handleAddBlankDescription(sectionIndex)} />
              </DescriptionAddIconWrapper>
            )}
          </>
        ))}
      </DescriptionsWrapper>
    </SectionContentWrapper>
  ));

  return (
    <Container>
      {categoryItems}
      {isModalShown && (
        <MessageModalPortal heading={modalMessage} onConfirm={hideMessageModal} onClose={hideMessageModal} />
      )}
    </Container>
  );
};

export default PortfolioSection;
