import { Post } from "../@types";
import { removeDuplicatedData } from "./data";

export const getPostsFromPages = (postsPages: (Post[] | null)[]) => {
  const posts = postsPages.map((page) => page ?? []).reduce((acc, postPage) => acc.concat(postPage), []);

  return removeDuplicatedData<Post>(posts, (post) => post.id);
};
