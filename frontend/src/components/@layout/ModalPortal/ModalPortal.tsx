import { createPortal } from "react-dom";
import { CSSProp } from "styled-components";
import { CancelNoCircleIcon } from "../../../assets/icons";
import Button from "../../@shared/Button/Button";
import BackDrop from "../../@styled/BackDrop";

import { CloseButtonWrapper, Container, ModalContent } from "./ModalPortal.style";

export interface Props {
  isCloseButtonShown?: boolean;
  children: React.ReactNode;
  cssProp?: CSSProp;
  onClose: () => void;
}

export const Modal = ({ isCloseButtonShown = false, children, cssProp, onClose }: Props) => {
  return (
    <Container cssProp={cssProp}>
      <BackDrop onClick={onClose} />
      <ModalContent>
        {isCloseButtonShown && (
          <CloseButtonWrapper>
            <Button kind="roundedInline" padding="0.5rem" onClick={onClose}>
              <CancelNoCircleIcon />
            </Button>
          </CloseButtonWrapper>
        )}
        {children}
      </ModalContent>
    </Container>
  );
};

const ModalPortal = ({ ...props }: Props) => {
  const $modal = document.getElementById("modal");

  if (!$modal) throw Error("cannot find modal wrapper");

  return createPortal(<Modal {...props} />, $modal);
};

export default ModalPortal;
