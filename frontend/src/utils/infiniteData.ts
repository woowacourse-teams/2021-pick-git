import { CommentData, GithubRepository, Post } from "../@types";

export const getPostsFromPages = (postsPages: Post[][]) => {
  return postsPages.reduce((acc, postPage) => acc.concat(postPage), []);
};

export const getRepositoriesFromPages = (postsPages?: GithubRepository[][]) => {
  return postsPages?.reduce((acc, postPage) => acc.concat(postPage), []) ?? null;
};

export const getCommentsFromPages = (postsPages: CommentData[][]) => {
  return postsPages.reduce((acc, postPage) => acc.concat(postPage), []);
};

export const getItemsFromPages = <Item>(pages?: (Item[] | null)[]) => {
  return (
    pages?.reduce((acc: Array<Item>, page) => {
      if (page === null) {
        return acc.concat([]);
      }

      return acc.concat(page);
    }, []) ?? null
  );
};
