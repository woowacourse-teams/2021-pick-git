import { useContext } from "react";

import SearchContext from "../../contexts/SearchContext";

const useSearchKeyword = () => {
  const { keyword, setKeyword } = useContext(SearchContext);

  const resetKeyword = () => {
    setKeyword("");
  };

  const changeKeyword = (newKeyword: string) => {
    setKeyword(newKeyword);
  };

  return { keyword, resetKeyword, changeKeyword };
};

export default useSearchKeyword;
