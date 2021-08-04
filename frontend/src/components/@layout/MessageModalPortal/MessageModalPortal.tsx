import { createPortal } from "react-dom";
import {
  ButtonsWrapper,
  ModalBody,
  Container,
  ModalContent,
  Text,
  CancelButton,
  ConfirmButton,
} from "./MessageModalPortal.styles";
import BackDrop from "../../@styled/BackDrop";

export interface Props {
  heading: string;
  onClose: () => void;
  onCancel?: () => void;
  onConfirm?: () => void;
}

export const MessageModal = ({ heading, onClose, onCancel, onConfirm }: Props) => {
  return (
    <Container>
      <BackDrop onMouseDown={onClose} />
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
