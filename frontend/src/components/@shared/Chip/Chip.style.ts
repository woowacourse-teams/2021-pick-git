import styled, { css, CSSProp } from "styled-components";

export const Container = styled.span<{ cssProp?: CSSProp; backgroundColor?: string }>(
  ({ theme, backgroundColor, cssProp }) => css`
    background-color: ${backgroundColor ?? theme.color.tagItemColor};
    color: ${theme.color.white};
    padding: 0.4375rem 0.9375rem;
    border-radius: 24px;
    display: inline-flex;
    align-items: center;
    ${cssProp}
  `
);

export const Text = styled.span(
  ({ theme }) => css`
    display: inline-flex;
    color: ${theme.color.white};
    align-items: center;
    font-size: 0.875rem;
    font-weight: bold;
  `
);

export const DeleteButton = styled.button`
  margin-left: 0.625rem;
  display: inline-flex;
  align-items: center;
`;
