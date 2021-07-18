/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useEffect, useState } from "react";
import useLocalStorage from "../services/hooks/@common/useLocalStorage";
import { requestGetSelfProfile } from "../services/requests";

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
  const { accessToken, username, setAccessToken, setUsername } = useLocalStorage();

  const login = (accessToken: string, username: string) => {
    setAccessToken(accessToken);
    setUsername(username);
    setIsLoggedIn(true);
  };

  const logout = () => {
    setAccessToken("");
    setUsername("");
    setIsLoggedIn(false);
  };

  useEffect(() => {
    if (!accessToken || !username) return;

    (async () => {
      try {
        const { name } = await requestGetSelfProfile(accessToken);

        setIsLoggedIn(true);
        setUsername(name);
      } catch (error) {
        console.error(error);
      }
    })();
  }, []);

  return (
    <UserContext.Provider value={{ currentUsername: username ?? "", isLoggedIn, login, logout }}>
      {children}
    </UserContext.Provider>
  );
};

export default UserContext;
