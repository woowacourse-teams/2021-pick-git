export const getImagePreloadPromise = (imageUrl: string) =>
  new Promise((resolve, reject) => {
    const image = new Image();

    image.src = imageUrl;
    image.onload = () => resolve(image);
    image.onerror = reject;
  });

export const getImagePreloadPromises = (imageUrls: string[]) => {
  return imageUrls.map(getImagePreloadPromise);
};
