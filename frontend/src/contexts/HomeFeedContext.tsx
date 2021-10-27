import { AxiosError } from "axios";
import { createContext, Dispatch, SetStateAction, useEffect, useState } from "react";
import { UseInfiniteQueryResult } from "react-query";

import { ErrorResponse, FeedFilterOption, Post } from "../@types";
import useAuth from "../hooks/common/useAuth";
import { useHomeFeedAllPostsQuery, useHomeFeedFollowingsPostsQuery } from "../services/queries";

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
  initHomeFeed: (feedFilterOption: FeedFilterOption) => void;
  setFeedFilterOption: Dispatch<SetStateAction<FeedFilterOption>>;
  setCurrentPostId: Dispatch<SetStateAction<number>>;
}

const HomeFeedContext = createContext<Value>({} as Value);

export const HomeFeedContextProvider = ({ children }: Props) => {
  const [feedFilterOption, setFeedFilterOption] = useState<FeedFilterOption>("all");
  const [currentPostId, setCurrentPostId] = useState(-1);
  const [initialized, setInitialized] = useState(false);
  const { isLoggedIn } = useAuth();

  const queryResults = {
    all: useHomeFeedAllPostsQuery(),
    followings: useHomeFeedFollowingsPostsQuery(),
  };

  const initHomeFeed = (feedFilterOption: FeedFilterOption) => {
    setFeedFilterOption(feedFilterOption);
    setInitialized(true);
  };

  useEffect(() => {
    initHomeFeed(isLoggedIn ? "followings" : "all");
  }, [isLoggedIn]);

  return (
    <HomeFeedContext.Provider
      value={{
        queryResults,
        feedFilterOption,
        currentPostId,
        initialized,
        initHomeFeed,
        setFeedFilterOption,
        setCurrentPostId,
      }}
    >
      {children}
    </HomeFeedContext.Provider>
  );
};

export default HomeFeedContext;
