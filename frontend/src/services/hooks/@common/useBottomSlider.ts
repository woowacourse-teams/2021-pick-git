import { useState } from "react";
import { useHistory } from "react-router-dom";
import { getLastHash } from "../../../utils/history";

const useBottomSlider = () => {
  const [isBottomSliderShown, setBottomSliderShown] = useState(false);
  const history = useHistory();

  const setSlideEventHandler = () => {
    window.onpopstate = () => {
      if (getLastHash(history.location.hash) === "slide-up") {
        setBottomSliderShown(true);
        return;
      }

      setBottomSliderShown(false);
    };
  };

  const removeSlideEventHandler = () => {
    window.onpopstate = null;
  };

  const showBottomSlider = () => {
    setBottomSliderShown(true);
    history.push("#slide-up");
  };

  const hideBottomSlider = () => {
    history.goBack();
  };

  return {
    isBottomSliderShown,
    setSlideEventHandler,
    removeSlideEventHandler,
    showBottomSlider,
    hideBottomSlider,
  };
};

export default useBottomSlider;
