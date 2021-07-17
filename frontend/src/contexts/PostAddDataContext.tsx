import { createContext, Dispatch, SetStateAction, useState } from "react";
import { GithubRepository } from "../@types";

interface Value {
  files: File[];
  githubRepositoryName: string;
  content: string;
  tags: string[];
  setFiles: Dispatch<SetStateAction<File[]>>;
  setGithubRepositoryName: Dispatch<SetStateAction<string>>;
  setContent: Dispatch<SetStateAction<string>>;
  setTags: Dispatch<SetStateAction<string[]>>;
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
  const [files, setFiles] = useState<File[]>([]);
  const [githubRepositoryName, setGithubRepositoryName] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [tags, setTags] = useState<string[]>([]);

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
