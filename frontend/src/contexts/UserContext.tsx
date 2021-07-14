/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useState } from "react";
import useLocalStorage from "../services/hooks/@common/useLocalStorage";

interface Props {
  children: React.ReactNode;
}

interface Value {
  isLoggedIn: boolean;
  currentUserName: string;
  login: (accessToken: string, userName: string) => void;
  logout: () => void;
}

const UserContext = createContext<Value>({
  currentUserName: "",
  isLoggedIn: false,
  login: () => {},
  logout: () => {},
});

export const UserContextProvider = ({ children }: Props) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUserName, setCurrentUserName] = useState("");
  const { setAccessToken } = useLocalStorage();

  const login = (accessToken: string, userName: string) => {
    setAccessToken(accessToken);
    setIsLoggedIn(true);
    setCurrentUserName(userName);
  };

  const logout = () => {
    setAccessToken("");
    setIsLoggedIn(false);
  };

  return <UserContext.Provider value={{ currentUserName, isLoggedIn, login, logout }}>{children}</UserContext.Provider>;
};

export default UserContext;
