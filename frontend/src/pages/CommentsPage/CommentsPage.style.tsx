import { Link } from "react-router-dom";
import styled from "styled-components";
import { Page } from "../../components/@styled/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery } from "../../components/@styled/mediaQueries";
import { LAYOUT, PAGE_WIDTH, Z_INDEX } from "../../constants/layout";

export const Container = styled(Page)`
  padding-top: 0;
  position: relative;
  padding-bottom: ${LAYOUT.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
`;

export const SliderHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  min-height: ${LAYOUT.HEADER_HEIGHT};
  padding: 1.0625rem 1.375rem;
`;

export const HorizontalSliderWrapper = styled.div`
  overflow-x: hidden;
`;

export const HorizontalSlider = styled.div<{ stepIndex: number; stepCount: number }>`
  position: relative;
  display: flex;
  transition: transform 0.5s;

  ${({ stepCount, stepIndex }) => `
    width: ${stepCount * 100}%;
    transform: translateX(${-((100 * stepIndex) / stepCount)}%);
  `}
`;

export const HorizontalSliderItemWrapper = styled.div<{ stepCount: number }>`
  ${({ stepCount }) => `
    width: ${100 / stepCount}%;
  `}
`;

export const PostContentAuthorLink = styled(Link)`
  margin-right: 0.4375rem;
  font-size: 0.75rem;
  font-weight: bold;
`;

export const PostContent = styled.p`
  font-size: 0.625rem;
  line-height: 1.5rem;
  padding: 0 1.125rem;
`;

export const TagListWrapper = styled.div`
  padding: 0 1.375rem;
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 1rem;
`;

export const TagItemLinkButton = styled(Link)`
  margin-right: 0.625rem;
  margin-bottom: 0.5625rem;
`;

export const GoBackLinkButton = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const CloseLinkButton = styled.a<{ isPostShown: boolean }>`
  transition: transform 0.5s, opacity 0.5s;
  transform: ${({ isPostShown }) => (isPostShown ? "" : "rotate(180deg)")};

  :hover {
    opacity: 0.5;
  }
`;

export const TabsWrapper = styled.div`
  width: 100%;
  padding: 1.125rem;
`;

export const CommentList = styled.ul`
  padding-top: 1.125rem;
  border-top: 1px solid ${({ theme }) => theme.color.secondaryColor};
`;

export const CommentListItem = styled.li`
  padding: 0 1.125rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.4375rem;
`;

export const CommentContentWrapper = styled.div`
  display: flex;
  align-items: center;
`;

export const CommentText = styled.span`
  margin-left: 0.5rem;
  font-size: 0.75rem;
`;

export const DeleteIconWrapper = styled.a`
  padding: 0.125rem;
  border-radius: 50%;
  background-color: ${({ theme }) => theme.color.lighterTextColor};
  transition: background-color 0.5s;
  opacity: 0.7;

  :hover {
    background-color: ${({ theme }) => theme.color.tertiaryColor};
  }
`;

export const SendIconWrapper = styled.div`
  cursor: pointer;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const CommentTextAreaWrapper = styled.div`
  position: fixed;
  bottom: 0;
  z-index: ${Z_INDEX.HIGH};
  width: 100%;
  display: flex;
  align-items: center;
  transition: background-color 0.5s;
  padding-right: 1rem;

  ${({ theme }) => `
    border-top: 1px solid ${theme.color.borderColor};
    background-color: ${theme.color.white};
    :focus-within {
      background-color: ${theme.color.secondaryColor};
    }
  `};

  ${setTabletMediaQuery`
    width: ${PAGE_WIDTH.TABLET};
    margin: 0 auto;
<<<<<<< HEAD
=======
  `}

  ${setLaptopMediaQuery`
    width: ${PAGE_WIDTH.LAPTOP};
    margin: 0 auto;
  `}

  ${setDesktopMediaQuery`
    width: ${PAGE_WIDTH.DESKTOP};
    margin: 0 auto;
  `} /* ${setTabletMediaQuery`
    padding: 0 3.5rem;
    border: none;
>>>>>>> ee70067 ([#457] 네번째 데모에서 발견된 버그를 해결한다. (#458))
  `}

  ${setLaptopMediaQuery`
    width: ${PAGE_WIDTH.LAPTOP};
    margin: 0 auto;
  `}

  ${setDesktopMediaQuery`
<<<<<<< HEAD
    width: ${PAGE_WIDTH.DESKTOP};
    margin: 0 auto;
  `}
=======
    padding: 0 24.5rem;
    border: none;
  `} */
>>>>>>> ee70067 ([#457] 네번째 데모에서 발견된 버그를 해결한다. (#458))
`;

export const CommentTextArea = styled.textarea`
  width: 100%;
  padding: 1.25rem 1rem 0rem 1rem;
  min-height: ${LAYOUT.COMMENT_INPUT_HEIGHT};
  font-size: 0.875rem;
  border: none;
  background-color: transparent;

  :focus {
    outline: none;
  }
`;
