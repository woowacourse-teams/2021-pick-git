import { createPortal } from "react-dom";
import { ButtonsWrapper, ModalBody, Container, ModalContent, Text, OKayButton } from "./AlertPortal.styles";
import BackDrop from "../../@styled/BackDrop";

export interface Props {
  heading: string;
  onOkay: () => void;
}

export const Alert = ({ heading, onOkay }: Props) => {
  return (
    <Container>
      <BackDrop />
      <ModalContent>
        <ModalBody>
          <Text>{heading}</Text>
          <ButtonsWrapper>
            <OKayButton onClick={onOkay}>확인</OKayButton>
          </ButtonsWrapper>
        </ModalBody>
      </ModalContent>
    </Container>
  );
};

const AlertPortal = ({ ...props }: Props) => {
  const $alert = document.getElementById("modal");
  if (!$alert) throw Error("cannot find modal wrapper");

  return createPortal(<Alert {...props} />, $alert);
};

export default AlertPortal;
