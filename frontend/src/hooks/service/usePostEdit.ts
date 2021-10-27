import { useContext, useState } from "react";
import { PostEditData } from "../../@types";
import PostEditDataContext from "../../contexts/PostEditDataContext";
import { getAccessToken } from "../../storage/storage";
import { requestEditPost } from "../../services/requests";

const usePostEdit = () => {
  const { postId, content, tags, setPostId, setContent, setTags } = useContext(PostEditDataContext);
  const [uploading, setUploading] = useState(false);

  const accessToken = getAccessToken();

  const editPost = async () => {
    setUploading(true);
    await requestEditPost({ postId, content, tags }, accessToken);
    setUploading(false);
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
    uploading,
  };
};

export default usePostEdit;
