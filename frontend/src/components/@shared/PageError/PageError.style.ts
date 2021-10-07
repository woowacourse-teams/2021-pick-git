import styled, { css, CSSProp } from "styled-components";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ theme, cssProp }) => css`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    ${cssProp}
  `
);

export const ErrorImage = styled.img`
  width: 70%;
  aspect-ratio: 4 / 3;
  max-width: 50rem;
  margin-bottom: 3rem;
`;

export const ErrorText = styled.h3(
  ({ theme }) => css`
    font-family: "jua";
    padding: 0 1rem;
    font-size: 1.2rem;
    text-align: center;
    color: ${theme.color.textColor};

    ${setLaptopAboveMediaQuery`
      font-size: 2rem;
    `}
  `
);
