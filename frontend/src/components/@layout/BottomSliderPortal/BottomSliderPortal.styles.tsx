import styled, { css } from "styled-components";
import { Z_INDEX } from "../../../constants/layout";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div<{ isSliderShown: boolean }>(
  ({ isSliderShown }) => css`
    position: fixed;
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    z-index: ${Z_INDEX.HIGH};
    bottom: 0;
    background-color: ${({ theme }) => theme.color.white};
    overflow: hidden;
    transition: transform 0.75s;
    transform: translateY(${isSliderShown ? "0%" : "100%"});
    box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.2);

    ${setLaptopAboveMediaQuery`
      border-top-left-radius: 24px;
      border-top-right-radius: 24px;
      width: 35rem;
      height: 70%;
      right: 7.5rem;
      transform: translateY(${isSliderShown ? "0%" : "100%"});
    `}
  `
);
