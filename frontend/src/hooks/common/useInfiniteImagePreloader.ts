import { useEffect, useState } from "react";
import { Post } from "../../@types";

import { getImagePreloadPromises } from "../../utils/preloaders";

const useInfiniteImagePreloader = (pages: Post["imageUrls"][]) => {
  const [loadIndex, setLoadIndex] = useState(0);
  const [isError, setIsError] = useState(false);
  const [isFirstImagesLoading, setIsFirstImagesLoading] = useState(true);
  const [isImagesFetching, setIsImagesFetching] = useState(false);

  const activateImageFetchingState = () => setIsImagesFetching(true);

  const deactivateLoadingStates = () => {
    setIsFirstImagesLoading(false);
    setIsImagesFetching(false);
  };

  const preloadImages = async (pageIndex: number) => {
    const imagePreLoadPromises = getImagePreloadPromises(pages[pageIndex]);

    await Promise.all(imagePreLoadPromises ?? []);
  };

  useEffect(() => {
    (async () => {
      const lastPagesIndex = (pages.length ?? 0) - 1;

      if (lastPagesIndex < loadIndex) {
        deactivateLoadingStates();
        setIsError(true);

        return;
      }

      for (let i = loadIndex; i <= lastPagesIndex; i++) {
        await preloadImages(i);
      }

      deactivateLoadingStates();
      setLoadIndex(lastPagesIndex + 1);
    })();
  }, [pages]);

  return { isFirstImagesLoading, isImagesFetching, activateImageFetchingState, isError };
};

export default useInfiniteImagePreloader;
