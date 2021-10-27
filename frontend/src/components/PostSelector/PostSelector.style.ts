import styled, { css } from "styled-components";
import { setLaptopAboveMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div`
  width: 20rem;
  height: 30rem;
  padding: 1rem 0;
  overflow-y: auto;

  ${setLaptopAboveMediaQuery`
    width: 33.75rem;
    height: 45rem;
  `}
`;

export const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  row-gap: 2px;
  column-gap: 2px;
`;

export const GridHeading = styled.h1``;

export const GridItem = styled.div<{ imageUrl: string }>(
  ({ imageUrl }) => css`
    width: 100%;
    padding-top: 100%;
    position: relative;
    cursor: pointer;

    :hover {
      filter: brightness(1.1);
    }

    :active {
      filter: brightness(0.9);
    }

    ::after {
      content: "";
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      background: url(${imageUrl}) center/cover;
    }
  `
);

export const NotFoundCSS = css`
  margin: 3rem 0;
  width: 100%;
`;
