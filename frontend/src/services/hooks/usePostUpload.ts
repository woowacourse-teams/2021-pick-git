import { useContext } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import UserContext from "../../contexts/UserContext";
import { requestAddPost } from "../requests";
import useLocalStorage from "./@common/useLocalStorage";

const usePostUpload = () => {
  const { accessToken } = useLocalStorage();
  const { content, files, tags, githubRepositoryName, setContent, setFiles, setGithubRepositoryName, setTags } =
    useContext(PostAddDataContext);
  const { currentUserName } = useContext(UserContext);

  const uploadPost = async () => {
    await requestAddPost(currentUserName, { content, files, tags, githubRepositoryName }, accessToken);
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
