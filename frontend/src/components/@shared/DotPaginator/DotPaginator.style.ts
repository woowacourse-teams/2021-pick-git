import styled from "styled-components";
import { setDesktopMediaQuery, setLaptopMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div<React.CSSProperties>`
  > div {
    :not(:last-child) {
      margin-bottom: 0.75rem;
    }
  }
`;

export const DotPagination = styled.div<{ isActive: boolean }>`
  width: 0.625rem;
  height: 0.625rem;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.5s;

  ${setLaptopMediaQuery`
    width: 0.8rem;
    height: 0.8rem;
  `}

  ${setDesktopMediaQuery`
    width: 1rem;
    height: 1rem;
  `}

  ${({ theme, isActive }) => `
    background-color: ${isActive ? theme.color.primaryColor : theme.color.secondaryColor};
    :hover {
      background-color: ${theme.color.primaryColor};
    }
  `};
`;
