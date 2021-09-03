import { useContext, useState } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import UserContext from "../../contexts/UserContext";
import { getAccessToken } from "../../storage/storage";
import { requestAddPost } from "../requests";

const usePostUpload = () => {
  const { content, files, tags, githubRepositoryName, setContent, setFiles, setGithubRepositoryName, setTags } =
    useContext(PostAddDataContext);
  const { currentUsername } = useContext(UserContext);
  const [uploading, setUploading] = useState(false);

  const accessToken = getAccessToken();

  const uploadPost = async () => {
    setUploading(true);
    await requestAddPost(currentUsername, { content, files, tags, githubRepositoryName }, accessToken);
    setUploading(false);
  };

  const resetPostUploadData = () => {
    setContent("");
    setFiles([]);
    setGithubRepositoryName("");
    setTags([]);
  };

  return {
    files,
    githubRepositoryName,
    content,
    tags,
    setFiles,
    setContent,
    setGithubRepositoryName,
    setTags,
    uploadPost,
    resetPostUploadData,
    uploading,
  };
};

export default usePostUpload;
