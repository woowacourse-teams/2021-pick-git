import { useState } from "react";

const useModal = (initialState = false) => {
  const [isModalShown, setIsModalShown] = useState(initialState);

  const showModal = () => setIsModalShown(true);
  const hideModal = () => setIsModalShown(false);

  return { isModalShown, showModal, hideModal };
};

export default useModal;
