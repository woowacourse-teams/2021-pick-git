import { AxiosError } from "axios";
import { QueryFunction, useInfiniteQuery, useQuery } from "react-query";
import { ErrorResponse, GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

type GithubTagsQueryKey = readonly [typeof QUERY.GET_GITHUB_TAGS, string];

export const useGithubRepositoriesQuery = (keyword: string) => {
  return useInfiniteQuery<GithubRepository[], AxiosError<ErrorResponse>, GithubRepository[], [string, string]>(
    [QUERY.GET_GITHUB_REPOSITORIES, keyword],
    async ({ pageParam = 0, queryKey }) => {
      const [, keywordParam] = queryKey;
      return requestGetRepositories(keywordParam, pageParam, getAccessToken());
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
      cacheTime: 0,
    }
  );
};

export const useGithubTagsQuery = (repositoryName: string) => {
  const githubTagsQueryFunction: QueryFunction<string[]> = async ({ queryKey }) => {
    const [, repositoryName] = queryKey as GithubTagsQueryKey;

    return await requestGetTags(repositoryName, getAccessToken());
  };

  return useQuery<Tags, AxiosError<ErrorResponse>>([QUERY.GET_GITHUB_TAGS, repositoryName], githubTagsQueryFunction, {
    cacheTime: 0,
  });
};
