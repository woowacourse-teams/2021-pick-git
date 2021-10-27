import { AxiosError } from "axios";
import { createContext, Dispatch, SetStateAction, useState } from "react";
import { UseInfiniteQueryResult } from "react-query";

import { ErrorResponse, FeedFilterOption, Post } from "../@types";
import { useHomeFeedPostsQuery } from "../services/queries";

interface Props {
  children: React.ReactNode;
}

interface Value {
  queryResult: UseInfiniteQueryResult<Post[], AxiosError<ErrorResponse>>;
  feedFilterOption: FeedFilterOption;
  currentPostId: string;
  initialized: boolean;
  initHomeFeed: (feedFilterOption: FeedFilterOption, postId?: string) => void;
  setFeedFilterOption: Dispatch<SetStateAction<FeedFilterOption>>;
  setCurrentPostId: Dispatch<SetStateAction<string>>;
}

const HomeFeedContext = createContext<Value>({} as Value);

export const HomeFeedContextProvider = ({ children }: Props) => {
  const [feedFilterOption, setFeedFilterOption] = useState<FeedFilterOption>("all");
  const [currentPostId, setCurrentPostId] = useState("");
  const [initialized, setInitialized] = useState(false);
  const queryResult = useHomeFeedPostsQuery(feedFilterOption);

  const initHomeFeed = (feedFilterOption: FeedFilterOption, postId?: string) => {
    setFeedFilterOption(feedFilterOption);
    setCurrentPostId(postId ?? "");
    setInitialized(true);
  };

  return (
    <HomeFeedContext.Provider
      value={{
        queryResult,
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
