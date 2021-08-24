import { Post } from "../../@types";
import { usePostLikePeopleQuery } from "../queries/postLikePeople";

const usePostLikePeople = (postId: Post["id"]) => {
  const { data: postLikePeople, isError, isLoading, refetch } = usePostLikePeopleQuery(postId);

  return {
    postLikePeople,
    isError,
    isLoading,
    refetch,
  };
};

export default usePostLikePeople;
