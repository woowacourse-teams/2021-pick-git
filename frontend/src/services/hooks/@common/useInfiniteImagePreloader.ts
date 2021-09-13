import { useEffect, useState } from "react";
import { Post } from "../../../@types";

import { getImagePreloadPromises } from "../../../utils/preloaders";

const useInfiniteImagePreloader = (pages: Post["imageUrls"][]) => {
  const [isFirstImagesLoading, setIsFirstImagesLoading] = useState(true);
  const [isImagesFetching, setIsImagesFetching] = useState(false);

  const activateImageFetchingState = () => setIsImagesFetching(true);

  const preloadImages = async (pageIndex: number) => {
    const imagePreLoadPromises = getImagePreloadPromises(pages[pageIndex]);

    await Promise.all(imagePreLoadPromises ?? []);
  };

  useEffect(() => {
    (async () => {
      const lastPagesIndex = (pages.length ?? 0) - 1;

      if (lastPagesIndex < 0) {
        return;
      }

      await preloadImages(lastPagesIndex);

      if (lastPagesIndex === 0) {
        setIsFirstImagesLoading(false);
      } else {
        setIsImagesFetching(false);
      }
    })();
  }, [pages]);

  return { isFirstImagesLoading, isImagesFetching, activateImageFetchingState };
};

export default useInfiniteImagePreloader;
