import { Post } from "../@types";

export const getPostsFromPages = (postsPages: Post[][]) => {
  return postsPages.reduce((acc, postPage) => acc.concat(postPage), []);
};
