import { useContext } from "react";

import SearchContext from "../../contexts/SearchContext";

const useSearchKeyword = () => {
  const { keyword, setKeyword } = useContext(SearchContext);

  const resetKeyword = () => {
    setKeyword("");
  };

  return { keyword, resetKeyword };
};

export default useSearchKeyword;
