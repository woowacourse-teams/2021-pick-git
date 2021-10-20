import { createPortal } from "react-dom";
import {
  ButtonsWrapper,
  ModalBody,
  Container,
  ModalContent,
  Text,
  CancelButton,
  ConfirmButton,
} from "./ConfirmPortal.styles";
import BackDrop from "../../@styled/BackDrop";

export interface Props {
  heading: string;
  onCancel?: () => void;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
}

export const Confirm = ({ heading, onCancel, onConfirm, confirmText = "확인", cancelText = "취소" }: Props) => {
  return (
    <Container>
      <BackDrop onMouseDown={onCancel} />
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

const ConfirmPortal = ({ ...props }: Props) => {
  const $confirm = document.getElementById("modal");
  if (!$confirm) throw Error("cannot find modal wrapper");

  return createPortal(<Confirm {...props} />, $confirm);
};

export default ConfirmPortal;
