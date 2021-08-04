import { useContext } from "react";
import { PostEditData } from "../../@types";
import PostEditDataContext from "../../contexts/PostEditDataContext";
import { getAccessToken } from "../../storage/storage";
import { requestEditPost } from "../requests";

const usePostEdit = () => {
  const { postId, content, tags, setPostId, setContent, setTags } = useContext(PostEditDataContext);

  const accessToken = getAccessToken();

  const editPost = async () => {
    await requestEditPost({ postId, content, tags }, accessToken);
  };

  const setPostEditData = ({ content, postId, tags }: PostEditData) => {
    setContent(content);
    setPostId(postId);
    setTags(tags);
  };

  const resetPostEditData = () => {
    setContent("");
    setPostId(0);
    setTags([]);
  };

  return {
    postId,
    content,
    tags,
    setContent,
    setTags,
    editPost,
    setPostEditData,
    resetPostEditData,
  };
};

export default usePostEdit;
