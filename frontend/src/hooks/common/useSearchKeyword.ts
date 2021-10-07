import { useEffect, useState } from "react";
import useDebounce from "./useDebounce";

const useSearchKeyword = () => {
  const [temporarySearchKeyword, setTemporarySearchKeyword] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");

  const changeSearchKeyword = useDebounce(() => {
    setSearchKeyword(temporarySearchKeyword);
  }, 150);

  useEffect(() => {
    changeSearchKeyword();
  }, [temporarySearchKeyword]);

  return {
    temporarySearchKeyword, searchKeyword, setTemporarySearchKeyword, setSearchKeyword, changeSearchKeyword
  }
}

export default useSearchKeyword