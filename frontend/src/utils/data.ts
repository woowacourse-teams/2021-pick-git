export const removeDuplicatedData = <TData>(data: TData[] | null, uniqueKeyGenerator: (data: TData) => unknown) => {
  const uniqueSet = new Set();

  return (
    data?.filter((currentData) => {
      const uniqueKey = uniqueKeyGenerator(currentData);
      const isNewPost = !uniqueSet.has(uniqueKey);

      if (isNewPost) {
        uniqueSet.add(uniqueKey);
      }

      return isNewPost;
    }) ?? []
  );
};
