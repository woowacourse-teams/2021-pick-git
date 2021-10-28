import { Link } from "react-router-dom";
import styled, { css } from "styled-components";
import { fadeIn } from "../../components/@styled/keyframes";
import { Page } from "../../components/@styled/layout";
import {
  setDesktopMediaQuery,
  setLaptopAboveMediaQuery,
  setLaptopMediaQuery,
  setTabletMediaQuery,
} from "../../components/@styled/mediaQueries";
import { customScrollbarCSS } from "../../components/@styled/scrollbar";
import { LAYOUT, PAGE_WIDTH, Z_INDEX } from "../../constants/layout";

export const ContentWrapper = styled.div(
  ({ theme }) => css`
    height: 100%;
    overflow-y: scroll;
    ${customScrollbarCSS(theme.color.textColor)}
  `
);

export const Container = styled(Page)(
  ({ theme }) => css`
    padding-top: 0;
    position: relative;
    padding-bottom: ${LAYOUT.HEADER_HEIGHT};
    background-color: ${theme.color.white};
  `
);

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
  display: flex;
  align-items: center;
  margin-right: 0.4375rem;
  height: 2.5rem;
  font-size: 0.75rem;
  font-weight: bold;

  ${setLaptopAboveMediaQuery`
    font-size: 1rem;
  `}
`;

export const CommentContent = styled.span(
  () => css`
    padding-top: 0.875rem;
    white-space: pre-wrap;
  `
);

export const PostContent = styled.p`
  font-size: 0.625rem;
  line-height: 1.5rem;
  padding: 0 1.125rem;

  ${setLaptopAboveMediaQuery`
    font-size: 0.9rem;
  `}
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

export const CloseLinkButtonWrapper = styled.div`
  display: flex;
  cursor: pointer;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const CloseLinkText = styled.span(
  ({ theme }) => css`
    margin-right: 0.75rem;
    color: ${theme.color.lighterTextColor};
  `
);

export const CloseLinkButton = styled.a<{ isPostShown: boolean }>`
  transition: transform 0.5s;
  transform: ${({ isPostShown }) => (isPostShown ? "" : "rotate(180deg)")};
`;

export const TabsWrapper = styled.div`
  width: 100%;
  padding: 1.125rem;
`;

export const NotFoundCSS = css`
  width: 70%;
  margin: 2rem auto 0;
`;

export const CommentList = styled.ul`
  padding-top: 1.125rem;
  border-top: 1px solid ${({ theme }) => theme.color.secondaryColor};
`;

export const CommentListItem = styled.li`
  padding: 0 1.125rem;
  display: flex;
  width: 100%;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.4375rem;

  animation: ${fadeIn} 1s forwards;
`;

export const CommentContentWrapper = styled.div`
  display: flex;
  width: 100%;
  align-items: flex-start;
`;

export const CommentTextWrapper = styled.div`
  display: flex;
  width: 100%;
  margin-left: 0.5rem;
  font-size: 0.75rem;
`;

export const DeleteIconWrapper = styled.a`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 23px;
  height: 23px;
  margin-left: 0.5rem;
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

export const CommentLoadingWrapper = styled.div`
  padding-top: 6.25rem;
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
  `}

  ${setLaptopMediaQuery`
    width: ${PAGE_WIDTH.LAPTOP};
    margin: 0 auto;
  `}

  ${setDesktopMediaQuery`
    width: ${PAGE_WIDTH.DESKTOP};
    margin: 0 auto;
  `}
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

export const LoaderWrapper = styled.div`
  display: flex;
  justify-content: center;
`;

export const LoaderCSS = css``;