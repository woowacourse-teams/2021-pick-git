import styled, { css, CSSProp } from "styled-components";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    ${cssProp}
  `
);

export const Image = styled.img`
  width: 70%;
  max-width: 50rem;
  margin-bottom: 3rem;
`;

export const Text = styled.h3(
  ({ theme }) => css`
    font-family: "jua";
    padding: 0 1rem;
    font-size: 1.2rem;
    text-align: center;
    color: ${theme.color.textColor};
    word-break: keep-all;

    ${setLaptopAboveMediaQuery`
      font-size: 2rem;
    `}
  `
);
