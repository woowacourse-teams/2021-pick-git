import styled, { css, CSSProp } from "styled-components";

export const Container = styled.button<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    margin: 0;
    padding: 0;
    position: relative;
    ${cssProp}
  `
);

export const ToggleLinkButton = styled.a`
  display: flex;
  align-items: center;
`;

export const ToggleLinkButtonText = styled.span``;

export const DropDownIconCSS = css`
  margin-left: 0.75rem;
`;

export const DropDownList = styled.ul<{ isShown: boolean }>(
  ({ theme, isShown }) => css`
    list-style: none;
    background-color: ${theme.color.white};
    position: absolute;
    top: 0;
    right: 0;
    display: flex;
    flex-direction: column;
    width: 140%;
    transform: translateY(2rem);
    transition: opacity 0.5s;
    box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.2);

    ${isShown ? `opacity: 1; pointer-events: initial` : `opacity: 0; pointer-events: none;`};
  `
);

export const DropDownListItem = styled.li`
  padding: 0;
  margin: 0;
  width: 100%;
  transition: opacity 0.5s;
  padding: 0.75rem;
  cursor: pointer;

  :hover {
    opacity: 0.5;
  }
`;
