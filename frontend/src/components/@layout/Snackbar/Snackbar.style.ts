import styled, { css } from "styled-components";
import { Z_INDEX } from "../../../constants/layout";
import { bottomToBottom } from "../../@styled/keyframes";

export const Container = styled.div<{ snackbarDuration: number } & React.CSSProperties>`
  display: flex;
  justify-content: center;
  align-items: center;

  position: fixed;
  left: 50%;
  bottom: ${({ bottom }) => bottom};
  opacity: 0;
  transform: translateX(-50%) translateY(50%);
  z-index: ${Z_INDEX.HIGH};

  min-width: 20rem;
  height: 2.3rem;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 0.5rem;
  font-size: 0.8rem;
  color: ${({ theme }) => theme.color.white};
  padding: 1rem 2rem;

  animation: ${({ animationDuration }) => css`
    ${bottomToBottom} ${animationDuration} ease
  `};
`;
