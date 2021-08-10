import { Post } from "../../@types";
import { usePostLikePeopleQuery } from "../queries/postLikePeople";

const usePostLikePeople = (postId: Post["id"]) => {
  const {
    data: infinitePostLikePeople,
    isError,
    isLoading,
    isFetching,
    fetchNextPage,
    refetch,
  } = usePostLikePeopleQuery(postId);

  const getNextPostLikePeople = () => {
    fetchNextPage();
  };

  return {
    infinitePostLikePeople,
    isError,
    isLoading,
    isFetching,
    getNextPostLikePeople,
    refetch,
  };
};

export default usePostLikePeople;
