import styled from "styled-components";
import { Z_INDEX } from "../../../constants/layout";
import { fadeIn } from "../../@styled/keyframes";

export const Container = styled.div`
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: ${Z_INDEX.MIDDLE};

  display: flex;
  justify-content: center;
  align-items: center;
  animation: ${fadeIn} 0.5s forwards;
`;

export const ModalContent = styled.div`
  width: fit-content;
  height: fit-content;
  padding: 0.75rem;
  border-radius: 4px;
  background-color: ${({ theme }) => theme.color.white};
`;

export const CloseButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;

  > button:active {
    transform: scale(0.98);
  }
`;
