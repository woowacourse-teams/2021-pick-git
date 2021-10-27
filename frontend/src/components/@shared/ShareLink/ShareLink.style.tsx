import styled, { css, CSSProp } from "styled-components";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    ${cssProp}
  `
);
