import styled, { css, keyframes } from "styled-components";

const BottomToBottom = keyframes`
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(50%);
  }
  
  25% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }

  75% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }

  to {
    opacity: 0;
    transform: translateX(-50%) translateY(50%);
  }
`;

export const Container = styled.div<{ snackbarDuration: number } & React.CSSProperties>`
  display: flex;
  justify-content: center;
  align-items: center;

  position: fixed;
  left: 50%;
  bottom: ${({ bottom }) => bottom};
  opacity: 0;
  transform: translateX(-50%) translateY(50%);

  min-width: 20rem;
  height: 2.3rem;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 0.5rem;
  font-size: 0.8rem;
  color: ${({ theme }) => theme.color.white};
  padding: 1rem 2rem;

  animation: ${({ animationDuration }) => css`
    ${BottomToBottom} ${animationDuration} ease
  `};
`;
