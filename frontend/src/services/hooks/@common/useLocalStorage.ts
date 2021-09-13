import { useEffect, useState } from "react";

const useLocalStorage = <T>(key: string) => {
  const isItemExist = (item: string | null): item is string => {
    return item !== null;
  };
  const getLocalStorageItem = (key: string): T | undefined => {
    const item = localStorage.getItem(key);
    if (!isItemExist(item)) {
      return;
    }

    return JSON.parse(item);
  };

  const [itemState, setItemState] = useState<T | undefined>(getLocalStorageItem(key));

  const setItem = (item: T) => {
    setItemState(item);
  };

  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(itemState ?? []));
  }, [itemState]);

  return {
    itemState,
    setItem,
  };
};

export default useLocalStorage;
