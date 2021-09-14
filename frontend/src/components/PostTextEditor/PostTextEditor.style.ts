import styled, { css, CSSProp } from "styled-components";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    ${cssProp}
    display: flex;
    flex-direction: column;
    align-items: flex-end;
  `
);

export const TextLengthIndicator = styled.div(
  ({ theme }) => css`
    margin-top: 0.5rem;
    color: ${theme.color.lighterTextColor};
    font-size: 0.8rem;
    float: right;

    display: flex;
    justify-content: flex-end;
    width: 3.5rem;
    height: 1rem;
  `
);

export const TextEditorCSS = css`
  width: 100%;
  height: 100%;
`;
