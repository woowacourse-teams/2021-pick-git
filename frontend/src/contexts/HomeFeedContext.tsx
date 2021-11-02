import { AxiosError } from "axios";
import { createContext, Dispatch, SetStateAction, useEffect, useState } from "react";
import { UseInfiniteQueryResult } from "react-query";

import { useHomeFeedAllPostsQuery, useHomeFeedFollowingsPostsQuery } from "../services/queries";

import { ErrorResponse, FeedFilterOption, Post } from "../@types";

interface Props {
  children: React.ReactNode;
}

interface Value {
  queryResults: {
    [K in FeedFilterOption]: UseInfiniteQueryResult<Post[] | null, AxiosError<ErrorResponse>>;
  };
  feedFilterOption: FeedFilterOption;
  currentPostId: number;
  initialized: boolean;
  initHomeFeed: () => void;
  refetchAll: () => void;
  setFeedFilterOption: Dispatch<SetStateAction<FeedFilterOption>>;
  setCurrentPostId: Dispatch<SetStateAction<number>>;
}

const HomeFeedContext = createContext<Value>({} as Value);

export const HomeFeedContextProvider = ({ children }: Props) => {
  const [feedFilterOption, setFeedFilterOption] = useState<FeedFilterOption>("all");
  const [currentPostId, setCurrentPostId] = useState(-1);
  const [initialized, setInitialized] = useState(false);

  const queryResults = {
    all: useHomeFeedAllPostsQuery(),
    followings: useHomeFeedFollowingsPostsQuery(),
  };

  const initHomeFeed = () => {
    setFeedFilterOption("all");
    setInitialized(true);
  };

  const refetchAll = () => {
    Object.values(queryResults).forEach((query) => query.refetch());
  };

  useEffect(() => {
    refetchAll();
  }, [feedFilterOption]);

  return (
    <HomeFeedContext.Provider
      value={{
        queryResults,
        feedFilterOption,
        currentPostId,
        initialized,
        initHomeFeed,
        refetchAll,
        setFeedFilterOption,
        setCurrentPostId,
      }}
    >
      {children}
    </HomeFeedContext.Provider>
  );
};

export default HomeFeedContext;
