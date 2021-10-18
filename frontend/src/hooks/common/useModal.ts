import { useState } from "react";

const useModal = (initialState = false) => {
  const [modalMessage, setModalMessage] = useState("");
  const [isModalShown, setIsModalShown] = useState(initialState);

  const showModal = (message?: string) => {
    message && setModalMessage(message);
    setIsModalShown(true);
  };

  const hideModal = () => setIsModalShown(false);

  return { isModalShown, modalMessage, showModal, hideModal };
};

export default useModal;
