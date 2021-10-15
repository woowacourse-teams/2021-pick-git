import { useContext, useState } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import UserContext from "../../contexts/UserContext";
import { getAccessToken } from "../../storage/storage";
import { requestAddPost } from "../../services/requests";

const usePostUpload = () => {
  const { content, files, tags, githubRepositoryName, setContent, setFiles, setGithubRepositoryName, setTags } =
    useContext(PostAddDataContext);
  const { currentUsername } = useContext(UserContext);
  const [uploading, setUploading] = useState(false);

  const accessToken = getAccessToken();

  const activateUploadingState = () => setUploading(true);
  const deactivateUploadingState = () => setUploading(false);

  const uploadPost = async () => {
    await requestAddPost(currentUsername, { content, files, tags, githubRepositoryName }, accessToken);
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
    activateUploadingState,
    deactivateUploadingState,
  };
};

export default usePostUpload;
