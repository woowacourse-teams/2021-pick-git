import styled from "styled-components";

export const Container = styled.div<{ isSliderShown: boolean }>`
  position: fixed;
  width: 100%;
  height: 100%;
  z-index: 100000000;
  bottom: 0;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  background-color: ${({ theme }) => theme.color.white};

  transition: transform 0.75s;

  ${({ isSliderShown }) => `
    transform: translateY(${isSliderShown ? "0%" : "100%"});
    box-shadow: ${isSliderShown ? "" : "0px -4px 8px rgba(0, 0, 0, 0.2)"};  
    border-radius: ${isSliderShown ? "0" : "initial"};
  `};
`;
