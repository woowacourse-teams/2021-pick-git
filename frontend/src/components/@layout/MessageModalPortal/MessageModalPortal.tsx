import BackDrop from "../../@shared/BackDrop/BackDrop";
import { createPortal } from "react-dom";
import Button from "../../@shared/Button/Button";
import {
  ButtonsWrapper,
  ModalBody,
  Container,
  ModalContent,
  Text,
  CancelButton,
  ConfirmButton,
} from "./MessageModalPortal.styles";
import { useContext } from "react";
import { ThemeContext } from "styled-components";

export interface Props {
  heading: string;
  onClose: () => void;
  onCancel?: () => void;
  onConfirm?: () => void;
}

export const MessageModal = ({ heading, onClose, onCancel, onConfirm }: Props) => {
  const { color } = useContext(ThemeContext);

  return (
    <Container>
      <BackDrop onBackDropClick={onClose} />
      <ModalContent>
        <ModalBody>
          <Text>{heading}</Text>
          <ButtonsWrapper>
            {onCancel && <CancelButton onClick={onCancel}>취소</CancelButton>}
            {onConfirm && <ConfirmButton onClick={onConfirm}>확인</ConfirmButton>}
          </ButtonsWrapper>
        </ModalBody>
      </ModalContent>
    </Container>
  );
};

const MessageModalPortal = ({ ...props }: Props) => {
  const $MessageModal = document.getElementById("modal");
  if (!$MessageModal) throw Error("cannot find modal wrapper");

  return createPortal(<MessageModal {...props} />, $MessageModal);
};

export default MessageModalPortal;
