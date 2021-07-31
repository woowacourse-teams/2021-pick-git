import { createContext, Dispatch, SetStateAction, useState } from "react";
import { PostEditData } from "../@types";

interface Value {
  postId: PostEditData["postId"];
  content: PostEditData["content"];
  tags: PostEditData["tags"];
  setPostId: Dispatch<SetStateAction<PostEditData["postId"]>>;
  setContent: Dispatch<SetStateAction<PostEditData["content"]>>;
  setTags: Dispatch<SetStateAction<PostEditData["tags"]>>;
}

const defaultValue = {
  postId: 0,
  content: "",
  tags: [],
  setPostId: () => {},
  setContent: () => {},
  setTags: () => {},
};

const PostEditDataContext = createContext<Value>(defaultValue);

export const PostEditDataContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [postId, setPostId] = useState<PostEditData["postId"]>(0);
  const [content, setContent] = useState<PostEditData["content"]>("");
  const [tags, setTags] = useState<PostEditData["tags"]>([]);

  return (
    <PostEditDataContext.Provider
      value={{
        postId,
        content,
        tags,
        setPostId,
        setContent,
        setTags,
      }}
    >
      {children}
    </PostEditDataContext.Provider>
  );
};

export default PostEditDataContext;
