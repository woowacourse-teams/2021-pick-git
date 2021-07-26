import { createContext, ReactNode, useEffect, useState } from "react";

import SnackBar from "../components/@layout/Snackbar/Snackbar";
import { MAX_STACK_NUM, SNACKBAR_DURATION } from "../constants/snackbar";

interface Props {
  children: ReactNode;
}

interface Value {
  pushSnackbarMessage: (message: string) => void;
}

const SnackBarContext = createContext<Value>({
  pushSnackbarMessage: () => {},
});

const keyGenerator = (() => {
  let id = 0;

  return () => id++;
})();

export const SnackBarContextProvider = ({ children }: Props) => {
  const [messages, setMessages] = useState<{ id: number; text: string }[]>([]);
  const messageIds = messages.join();

  const pushSnackbarMessage = (message: string): void =>
    setMessages((messages) => {
      const newMessages = [...messages, { id: keyGenerator(), text: message }];

      if (newMessages.length === MAX_STACK_NUM + 1) {
        return newMessages.slice(1);
      }

      return newMessages;
    });

  useEffect(() => {
    if (messageIds.length > 0) {
      const timer = setTimeout(() => {
        setMessages((messages) => messages.slice(1));
      }, SNACKBAR_DURATION);

      return () => clearTimeout(timer);
    }
  }, [messageIds]);

  return (
    <SnackBarContext.Provider value={{ pushSnackbarMessage }}>
      {children}
      {messages.map((message, index, arr) => (
        <SnackBar key={message.id} order={arr.length - index}>
          {message.text}
        </SnackBar>
      ))}
    </SnackBarContext.Provider>
  );
};

export default SnackBarContext;
