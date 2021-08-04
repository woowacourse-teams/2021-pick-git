import { Post } from "../@types";
import { removeDuplicatedData } from "./data";

export const getPostsFromPages = (postsPages: Post[][]) => {
  const posts = postsPages.reduce((acc, postPage) => acc.concat(postPage), []);

  return removeDuplicatedData<Post>(posts, (post) => post.id);
};
