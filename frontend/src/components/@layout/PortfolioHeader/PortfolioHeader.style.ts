import styled, { css } from "styled-components";
import { Header } from "../../@styled/layout";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";
import { Z_INDEX } from "../../../constants/layout";

export const Container = styled(Header)`
  display: flex;
  align-items: center;
  position: relative;
  z-index: ${Z_INDEX.LOW};
  padding: 1.1875rem 1.4375rem;
  width: 100%;
`;

export const HeaderContentWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  ${setLaptopAboveMediaQuery`
    padding: 0 3rem;
  `}
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

export const KaKaoShareButton = styled.button(
  ({ theme }) => css`
    border-radius: 50%;
    padding: 0.5rem 0.4rem 0.3125rem 0.5rem;
    overflow: hidden;
    background-color: ${theme.color.yellow};
    box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
    transition: opacity 0.5s;

    :hover {
      opacity: 0.5;
    }
  `
);

export const LinkShareButton = styled.button(
  () => css`
    border-radius: 50%;
    padding: 0.5rem;
    overflow: hidden;
    margin-right: 0.75rem;
    box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
    transition: opacity 0.5s;

    :hover {
      opacity: 0.5;
    }
  `
);