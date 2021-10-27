import { createPortal } from "react-dom";
import {
  ButtonsWrapper,
  ModalBody,
  Container,
  ModalContent,
  Text,
  NagativeButton,
  PositiveButton,
} from "./ChoiceModalPortal.styles";
import BackDrop from "../../@styled/BackDrop";

export interface Props {
  heading: string;
  onPositiveChoose?: () => void;
  onNagativeChoose?: () => void;
  onClose: () => void;
  positiveChoiceText?: string;
  nagativeChoiceText?: string;
}

export const ChoiceModal = ({
  heading,
  onPositiveChoose,
  onNagativeChoose,
  onClose,
  positiveChoiceText,
  nagativeChoiceText,
}: Props) => {
  return (
    <Container>
      <BackDrop onMouseDown={onClose} />
      <ModalContent>
        <ModalBody>
          <Text>{heading}</Text>
          <ButtonsWrapper>
            {onNagativeChoose && <NagativeButton onClick={onNagativeChoose}>{nagativeChoiceText}</NagativeButton>}
            {onPositiveChoose && <PositiveButton onClick={onPositiveChoose}>{positiveChoiceText}</PositiveButton>}
          </ButtonsWrapper>
        </ModalBody>
      </ModalContent>
    </Container>
  );
};

const ChoiceModalPortal = ({ ...props }: Props) => {
  const $choiceModal = document.getElementById("modal");
  if (!$choiceModal) throw Error("cannot find modal wrapper");

  return createPortal(<ChoiceModal {...props} />, $choiceModal);
};

export default ChoiceModalPortal;
