import { useState } from "react";
import { useHistory } from "react-router-dom";
import { getLastHash } from "../../utils/history";

const useBottomSlider = () => {
  const [isBottomSliderShown, setBottomSliderShown] = useState(false);
  const history = useHistory();

  const setSlideEventHandler = () => {
    window.onpopstate = () => {
      if (getLastHash(history.location.hash) === "comment-slider-up") {
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
    history.push({
      hash: "#comment-slider-up",
      search: history.location.search,
      state: history.location.state,
    });

    setBottomSliderShown(true);
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
