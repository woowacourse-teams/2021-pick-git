import styled, { css } from "styled-components";
import { fadeIn } from "../@styled/keyframes";
import { setLaptopAboveMediaQuery } from "../@styled/mediaQueries";
import { customScrollbarCSS } from "../@styled/scrollbar";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 1.6875rem;
`;

export const Form = styled.form`
  margin-top: 3rem;
  margin-bottom: 1.8125rem;

  ${setLaptopAboveMediaQuery`
    margin-top: 6rem;
  `}
`;

export const TagList = styled.ul(
  ({ theme }) => css`
    display: flex;
    flex-wrap: wrap;
    margin: 0;
    overflow-y: scroll;

    ${customScrollbarCSS(theme.color.tagItemColor)}
  `
);

export const TagListItem = styled.li`
  margin: 0 0.625rem 0.5625rem 0;

  animation: ${fadeIn} 1s forwards;
`;

export const TagInputWrapper = styled.div`
  position: relative;
`;

export const TagInputCSS = css`
  line-height: 1rem;
`;

export const TagAddButton = styled.button`
  position: absolute;
  top: 0.625rem;
  right: 0.3125rem;
`;

export const TextLengthIndicator = styled.div<React.CSSProperties>`
  margin-top: 0.5rem;
  color: ${({ theme }) => theme.color.lighterTextColor};
  font-size: 0.8rem;
  float: right;
  height: 1rem;
`;
