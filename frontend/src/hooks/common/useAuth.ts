import { useContext } from "react";

import UserContext from "../../contexts/UserContext";

const useAuth = () => {
  const { currentUsername, isLoggedIn, login, logout } = useContext(UserContext);

  return {
    currentUsername,
    isLoggedIn,
    login,
    logout,
  };
};

export default useAuth;
