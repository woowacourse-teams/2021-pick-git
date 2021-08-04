import { useContext, useEffect } from "react";
import UserContext, { UserContextProvider } from "../../src/contexts/UserContext";

const LoggedInWrapper = ({ children }: { children: React.ReactNode }) => {
  const Inner = () => {
    const { login } = useContext(UserContext);

    useEffect(() => login("test", "Tanney"), []);

    return <></>;
  };

  return (
    <UserContextProvider>
      <Inner />
      {children}
    </UserContextProvider>
  );
};

export default LoggedInWrapper;