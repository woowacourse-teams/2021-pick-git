import { createContext, Dispatch, SetStateAction, useState } from "react";

interface Props {
  children: React.ReactNode;
}

interface Value {
  keyword: string;
  setKeyword: Dispatch<SetStateAction<string>>;
}

const SearchContext = createContext<Value>({
  keyword: "",
  setKeyword: () => {},
});

export const SearchContextProvider = ({ children }: Props) => {
  const [keyword, setKeyword] = useState("");

  return <SearchContext.Provider value={{ keyword, setKeyword }}>{children}</SearchContext.Provider>;
};

export default SearchContext;
