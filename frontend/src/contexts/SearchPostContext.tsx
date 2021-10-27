import { AxiosError } from "axios";
import { Children, createContext, useState } from "react";
import { UseInfiniteQueryResult } from "react-query";
import { ErrorResponse, Post } from "../@types";
import { useSearchPostResultQuery } from "../services/queries/search";

interface Props {
  children: React.ReactNode;
}

interface Value {
  queryResult: UseInfiniteQueryResult<Post[] | null, AxiosError<ErrorResponse>>;
  initialized: boolean;
  initSearchPost: (type: string, keyword: string, activated: boolean) => void;
}

const SearchPostContext = createContext({} as Value);

export const SearchPostContextProvider = ({ children }: Props) => {
  const [type, setType] = useState("");
  const [keyword, setKeyword] = useState("");
  const [activated, setActivated] = useState(false);
  const [initialized, setInitialized] = useState(false);
  const queryResult = useSearchPostResultQuery(type, keyword, activated);

  const initSearchPost = (type: string, keyword: string, activated: boolean) => {
    setType(type);
    setKeyword(keyword);
    setActivated(activated);
    setInitialized(true);
  };

  return (
    <SearchPostContext.Provider value={{ queryResult, initialized, initSearchPost }}>
      {children}
    </SearchPostContext.Provider>
  );
};

export default SearchPostContext;
