import { useGetPostQuery } from "../../services/queries";

const usePostDetail = (postId: number, activated: boolean = false) => {
  const { data: post, isLoading, isError } = useGetPostQuery(postId, activated);

  return {
    post,
    isLoading,
    isError,
  };
};

export default usePostDetail;
