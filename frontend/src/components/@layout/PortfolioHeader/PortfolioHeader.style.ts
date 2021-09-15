import styled, { css } from "styled-components";
import { Header } from "../../@styled/layout";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled(Header)`
  display: flex;
  align-items: center;
  position: static;
  padding: 1.1875rem 1.4375rem;
  width: 100%;
`;

export const HeaderContentWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 0 3rem;
`;

export const HeaderButtonsWrapper = styled.div`
  display: flex;
`;

export const DropDownCSS = css`
  margin-right: 1rem;

  ${setLaptopAboveMediaQuery`
    margin-right: 2rem;
  `}
`;

export const GoBackLinkButton = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;
