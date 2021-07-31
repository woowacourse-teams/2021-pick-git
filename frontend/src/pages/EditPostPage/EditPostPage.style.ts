import styled from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)<React.CSSProperties>`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow-x: hidden;
  background-color: ${({ theme }) => theme.color.white};
`;

export const StepSlider = styled.div<{ stepIndex: number; stepCount: number }>`
  display: flex;
  flex-grow: 1;
  transition: transform 0.5s;
  height: 100%;

  ${({ stepCount, stepIndex }) => `
    width: ${stepCount * 100}%;
    transform: translateX(${-((100 * stepIndex) / stepCount)}%);
  `}
`;

export const StepContainer = styled.div<{ stepCount: number; isShown: boolean }>`
  transition: opacity 2s;

  ${({ stepCount, isShown }) => `
    width: ${100 / stepCount}%;
    opacity: ${isShown ? 1 : 0};
  `}
`;

export const NextStepButtonWrapper = styled.div`
  padding: 0 3rem;
  margin: 2.5rem 0;
`;
