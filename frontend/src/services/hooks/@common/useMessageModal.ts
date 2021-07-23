import { useState } from "react";

const useMessageModal = () => {
  const [modalMessage, setModalMessage] = useState("");
  const [isCancelButtonShown, setIsCancelButtonShown] = useState(false);
  const [isModalShown, setIsModalShown] = useState(false);

  const showAlertModal = (message: string) => {
    if (isModalShown) {
      return;
    }

    setIsCancelButtonShown(false);
    setModalMessage(message);
    setIsModalShown(true);
  };

  const showConfirmModal = (message: string) => {
    if (isModalShown) {
      return;
    }

    setIsCancelButtonShown(true);
    setModalMessage(message);
    setIsModalShown(true);
  };

  const hideMessageModal = () => {
    if (!isModalShown) {
      return;
    }

    setModalMessage("");
    setIsModalShown(false);
  };

  return {
    modalMessage,
    isModalShown,
    isCancelButtonShown,
    showAlertModal,
    showConfirmModal,
    hideMessageModal,
  };
};

export default useMessageModal;
