import { createContext, Dispatch, SetStateAction, useState } from "react";

interface Props {
  children: React.ReactNode;
}

interface Value {
  keyword: string;
  setKeyword: Dispatch<SetStateAction<string>>;
  onKeywordChange: (value: string) => void;
}

const SearchContext = createContext<Value>({
  keyword: "",
  setKeyword: () => {},
  onKeywordChange: (value: string) => {},
});

export const SearchContextProvider = ({ children }: Props) => {
  const [keyword, setKeyword] = useState("");

  const handleKeywordChange = (value: string) => setKeyword(value);

  return (
    <SearchContext.Provider value={{ keyword, setKeyword, onKeywordChange: handleKeywordChange }}>
      {children}
    </SearchContext.Provider>
  );
};

export default SearchContext;
