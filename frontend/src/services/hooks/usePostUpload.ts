import { useContext } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import UserContext from "../../contexts/UserContext";
import { requestAddPost } from "../requests";
import storage from "../../storage/storage";

const usePostUpload = () => {
  const { getAccessToken } = storage();
  const { content, files, tags, githubRepositoryName, setContent, setFiles, setGithubRepositoryName, setTags } =
    useContext(PostAddDataContext);
  const { currentUsername } = useContext(UserContext);

  const accessToken = getAccessToken();

  const uploadPost = async () => {
    await requestAddPost(currentUsername, { content, files, tags, githubRepositoryName }, accessToken);
  };

  const resetUploadData = () => {
    setContent("");
    setFiles([]);
    setGithubRepositoryName("");
    setTags([]);
  };

  return {
    uploadPost,
    resetUploadData,
  };
};

export default usePostUpload;
