import styled, { css } from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)<React.CSSProperties>`
  position: relative;
  height: 100%;
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
  /* TODO : Calc 안 쓰는 방법 생각해보기 */
  height: Calc(100% - 7.5rem);

  ${({ stepCount, stepIndex }) => `
    width: ${stepCount * 100}%;
    transform: translateX(${-((100 * stepIndex) / stepCount)}%);
  `}
`;

export const StepContainer = styled.div<{ stepCount: number; isShown: boolean }>(
  ({ stepCount, isShown }) => css`
    transition: opacity 0.5s;
    width: ${100 / stepCount}%;
    opacity: ${isShown ? 1 : 0};
    pointer-events: ${isShown ? "initial" : "none"};
  `
);

export const NextStepButtonWrapper = styled.div`
  padding: 0 3rem;
  margin: 2.5rem 0;
`;
