import { AxiosError } from "axios";
import { createContext, useState } from "react";
import { UseInfiniteQueryResult } from "react-query";

import type { ErrorResponse, Post } from "../@types";
import { useUserPostsQuery } from "../services/queries";

interface Props {
  children: React.ReactNode;
}

interface Value {
  queryResult: UseInfiniteQueryResult<Post[], AxiosError<ErrorResponse>>;
  initialized: boolean;
  initUserFeed: (isMyFeed: boolean, username: string | null) => void;
}

const UserFeedContext = createContext<Value>({} as Value);

export const UserFeedContextProvider = ({ children }: Props) => {
  const [isMyFeed, setIsMyFeed] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
  const [initialized, setInitialized] = useState(false);
  const queryResult = useUserPostsQuery(isMyFeed, username);

  const initUserFeed = (isMyFeed: boolean, username: string | null) => {
    setIsMyFeed(isMyFeed);
    setUsername(username);
    setInitialized(true);
  };

  return (
    <UserFeedContext.Provider value={{ queryResult, initialized, initUserFeed }}>{children}</UserFeedContext.Provider>
  );
};

export default UserFeedContext;
