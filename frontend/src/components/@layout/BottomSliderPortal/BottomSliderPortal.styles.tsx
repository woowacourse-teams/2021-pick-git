import styled, { css } from "styled-components";
import { Z_INDEX } from "../../../constants/layout";

export const Container = styled.div<{ isSliderShown: boolean }>(
  ({ isSliderShown }) => css`
    position: fixed;
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    z-index: ${Z_INDEX.HIGH};
    bottom: 0;
    border-top-left-radius: 16px;
    border-top-right-radius: 16px;
    background-color: ${({ theme }) => theme.color.white};

    transition: transform 0.75s;

    transform: translateY(${isSliderShown ? "0%" : "100%"});
    border-radius: ${isSliderShown ? "0" : "initial"};
  `
);
