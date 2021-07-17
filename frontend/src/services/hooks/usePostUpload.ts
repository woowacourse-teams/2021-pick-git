import { useContext } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import { requestAddPost } from "../requests";
import useLocalStorage from "./@common/useLocalStorage";

const usePostUpload = () => {
  const { accessToken } = useLocalStorage();
  const { content, files, tags, githubRepositoryName, setContent, setFiles, setGithubRepositoryName, setTags } =
    useContext(PostAddDataContext);

  const uploadPost = async () => {
    await requestAddPost({ content, files, tags, githubRepositoryName }, accessToken);
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
