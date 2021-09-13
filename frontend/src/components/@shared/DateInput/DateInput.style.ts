import styled, { css, CSSProp } from "styled-components";

export const Input = styled.input<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    ${cssProp}
    border: none;
  `
);
