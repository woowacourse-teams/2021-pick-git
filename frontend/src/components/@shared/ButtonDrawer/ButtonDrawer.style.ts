import styled from "styled-components";
import { Z_INDEX } from "../../../constants/layout";

export const Container = styled.span<React.CSSProperties>`
  position: relative;
  display: inline-flex;
  justify-content: center;
  align-items: center;
`;

export const IconWrapper = styled.div`
  cursor: pointer;
`;

export const CircleButton = styled.div<{
  isShown: boolean;
  index: number;
  buttonsCount: number;
}>`
  position: absolute;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  border-radius: 50%;
  width: 2.2rem;
  height: 2.2rem;
  background-color: ${({ theme }) => theme.color.white};
  box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
  transition: box-shadow 0.5s, opacity 0.5s, transform 0.5s;
  visibility: hidden;
  opacity: 0;
  z-index: ${Z_INDEX.LOW};

  :hover {
    box-shadow: 2px 6px 12px rgba(0, 0, 0, 0.2);
  }

  ${({ isShown, index, buttonsCount }) => `
    visibility: ${isShown ? "visible" : "hidden"};
    opacity: ${isShown ? "1" : "0"};
    transform: ${
      isShown ? `translate(${(-3.5 * index) / buttonsCount - 0.3}rem, ${(-3.5 * index) / buttonsCount + 1.2}rem)` : ""
    };
`}
`;
