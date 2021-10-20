import styled from "styled-components";
import { Z_INDEX } from "../../../constants/layout";
import { fadeIn } from "../../@styled/keyframes";

export const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  width: 100vw;
  height: 100vh;
  z-index: ${Z_INDEX.HIGHEST};
  animation: ${fadeIn} 0.5s forwards;
`;

export const ModalContent = styled.div`
  width: 90%;
  max-width: 25rem;
  height: 30%;
  border-radius: 8px;
  box-shadow: 5px 5px 25px rgba(0, 0, 0, 0.2);
  background-color: ${({ theme }) => theme.color.white};
  overflow: hidden;
`;

export const ModalBody = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
`;

export const Text = styled.span`
  flex-grow: 1;
  display: flex;
  font-size: 1.1rem;
  line-height: 1.75rem;
  text-align: center;
  justify-content: center;
  align-items: center;
  letter-spacing: 0.7px;
  margin-top: 1rem;
  padding: 0 1.5rem;
  word-break: keep-all;
`;

const Button = styled.button`
  width: 100%;
  text-align: center;
  padding: 1rem;
`;

export const PositiveButton = styled(Button)`
  ${({ theme }) => `
    background-color: ${theme.color.primaryColor}};
    color: ${theme.color.white}};
  `}
`;

export const NagativeButton = styled(Button)`
  ${({ theme }) => `
    background-color: ${theme.color.lighterTextColor}};
    color: ${theme.color.white}};
  `}
`;

export const ButtonsWrapper = styled.div`
  width: 100%;
  display: flex;
`;
