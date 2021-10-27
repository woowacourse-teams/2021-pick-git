import styled, { css } from "styled-components";
import { setDesktopMediaQuery, setLaptopMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div`
  overflow-y: auto;
  ${setLaptopMediaQuery`
    margin-top: 1rem;
  `}

  ${setDesktopMediaQuery`
    margin-top: 1rem;
  `}
`;

export const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  row-gap: 2px;
  column-gap: 2px;
`;

export const GridItem = styled.div<{ imageUrl: string }>`
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
    background: url(${({ imageUrl }) => imageUrl}) center/cover;
  }
`;

export const NotFoundCSS = css`
  margin: 3rem auto;
  width: 100%;
`;
