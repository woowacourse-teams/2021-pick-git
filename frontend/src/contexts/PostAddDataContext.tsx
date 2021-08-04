import { createContext, Dispatch, SetStateAction, useState } from "react";
import { PostUploadData } from "../@types";

interface Value {
  files: PostUploadData["files"];
  githubRepositoryName: PostUploadData["githubRepositoryName"];
  content: PostUploadData["content"];
  tags: PostUploadData["tags"];
  setFiles: Dispatch<SetStateAction<PostUploadData["files"]>>;
  setGithubRepositoryName: Dispatch<SetStateAction<PostUploadData["githubRepositoryName"]>>;
  setContent: Dispatch<SetStateAction<PostUploadData["content"]>>;
  setTags: Dispatch<SetStateAction<PostUploadData["tags"]>>;
}

const defaultValue = {
  files: [],
  githubRepositoryName: "",
  content: "",
  tags: [],
  setFiles: () => {},
  setGithubRepositoryName: () => {},
  setContent: () => {},
  setTags: () => {},
};

const PostAddDataContext = createContext<Value>(defaultValue);

export const PostAddDataContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [files, setFiles] = useState<PostUploadData["files"]>([]);
  const [githubRepositoryName, setGithubRepositoryName] = useState<PostUploadData["githubRepositoryName"]>("");
  const [content, setContent] = useState<PostUploadData["content"]>("");
  const [tags, setTags] = useState<PostUploadData["tags"]>([]);

  return (
    <PostAddDataContext.Provider
      value={{
        files,
        githubRepositoryName,
        content,
        tags,
        setFiles,
        setGithubRepositoryName,
        setContent,
        setTags,
      }}
    >
      {children}
    </PostAddDataContext.Provider>
  );
};

export default PostAddDataContext;
