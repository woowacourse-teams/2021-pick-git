import { createContext, useState } from "react";
import { getUsername, setAccessToken, setUsername } from "../storage/storage";

interface Props {
  children: React.ReactNode;
}

interface Value {
  isLoggedIn: boolean;
  currentUsername: string;
  login: (accessToken: string, username: string) => void;
  logout: () => void;
}

const UserContext = createContext<Value>({
  currentUsername: "",
  isLoggedIn: false,
  login: () => {},
  logout: () => {},
});

export const UserContextProvider = ({ children }: Props) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUsername, setCurrentUsername] = useState(getUsername() ?? "");

  const login = (accessToken: string, username: string) => {
    setAccessToken(accessToken);
    setUsername(username);
    setIsLoggedIn(true);
    setCurrentUsername(username);
  };

  const logout = () => {
    setAccessToken("");
    setUsername("");
    setIsLoggedIn(false);
    setCurrentUsername("");
  };

  return <UserContext.Provider value={{ currentUsername, isLoggedIn, login, logout }}>{children}</UserContext.Provider>;
};

export default UserContext;
