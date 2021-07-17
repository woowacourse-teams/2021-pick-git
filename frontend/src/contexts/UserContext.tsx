/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useEffect, useState } from "react";
import useLocalStorage from "../services/hooks/@common/useLocalStorage";
import { requestGetSelfProfile } from "../services/requests";

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
  const { accessToken, userName, setAccessToken, setUserName } = useLocalStorage();

  const login = (accessToken: string, userName: string) => {
    setAccessToken(accessToken);
    setUserName(userName);
    setIsLoggedIn(true);
  };

  const logout = () => {
    setAccessToken("");
    setUserName("");
    setIsLoggedIn(false);
  };

  useEffect(() => {
    if (!accessToken || !userName) return;

    (async () => {
      try {
        const { name } = await requestGetSelfProfile(accessToken);

        setIsLoggedIn(true);
        setUserName(name);
      } catch (error) {
        console.error(error);
      }
    })();
  }, []);

  return (
    <UserContext.Provider value={{ currentUserName: userName ?? "", isLoggedIn, login, logout }}>
      {children}
    </UserContext.Provider>
  );
};

export default UserContext;
