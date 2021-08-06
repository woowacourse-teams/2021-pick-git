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
  confirmText?: string;
  cancelText?: string;
}

export const MessageModal = ({
  heading,
  onClose,
  onCancel,
  onConfirm,
  confirmText = "확인",
  cancelText = "취소",
}: Props) => {
  return (
    <Container>
      <BackDrop onMouseDown={onClose} />
      <ModalContent>
        <ModalBody>
          <Text>{heading}</Text>
          <ButtonsWrapper>
            {onCancel && <CancelButton onClick={onCancel}>{cancelText}</CancelButton>}
            {onConfirm && <ConfirmButton onClick={onConfirm}>{confirmText}</ConfirmButton>}
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
