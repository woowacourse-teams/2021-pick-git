import { Link } from "react-router-dom";
import styled from "styled-components";
import { LAYOUT } from "../../constants/layout";

export const Container = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 100%;
  padding-bottom: ${LAYOUT.HEADER_HEIGHT};
  overflow-x: scroll;
  overflow-y: scroll;
`;

export const SliderHeader = styled.div`
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  min-height: ${LAYOUT.HEADER_HEIGHT};
  padding: 1.0625rem 1.375rem;
`;

export const HorizontalSlider = styled.div<{ stepIndex: number; stepCount: number }>`
  position: relative;
  left: 100%;
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

export const PostContent = styled.span`
  font-size: 0.625rem;
  line-height: 1.5rem;
  padding: 0 1.375rem;
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

export const CloseButton = styled.a``;

export const TabsWrapper = styled.div`
  width: 100%;
  padding: 1.125rem;
  border-bottom: 1px solid ${({ theme }) => theme.color.secondaryColor};
  margin-bottom: 1.125rem;
`;

export const CommentList = styled.ul`
  width: 100%;
`;

export const CommentListItem = styled.li`
  padding: 0 1.125rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.4375rem;
`;

export const CommentContent = styled.div`
  display: flex;
  align-items: center;
`;

export const CommentText = styled.span`
  margin-left: 0.5rem;
  font-size: 0.75rem;
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
  width: 100%;
  display: flex;
  align-items: center;
  transition: background-color 0.5s;

  ${({ theme }) => `
    border-top: 1px solid ${theme.color.borderColor};
    background-color: ${theme.color.white};
    :focus-within {
      background-color: ${theme.color.secondaryColor};
    }
  `};
  padding-right: 1rem;
`;

export const CommentTextArea = styled.textarea`
  width: 100%;
  padding: 1rem 1rem 0rem 1rem;
  min-height: ${LAYOUT.COMMENT_INPUT_HEIGHT};
  font-size: 0.875rem;
  border: none;
  background-color: transparent;

  :focus {
    outline: none;
  }
`;
