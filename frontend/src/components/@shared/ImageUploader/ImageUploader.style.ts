import styled, { css, CSSProp } from "styled-components";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    display: flex;
    justify-content: center;
    width: 100%;
    ${cssProp}
  `
);

export const Image = styled.img`
  width: 100%;
  transition: opacity 0.5s, box-shadow 0.5s;
  cursor: pointer;
  object-fit: cover;

  :hover {
    opacity: 0.85;
    box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.2);
  }
`;
