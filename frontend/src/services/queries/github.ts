import { AxiosError } from "axios";
import { QueryFunction, useQuery } from "react-query";
import { ErrorResponse, GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

type GithubTagsQueryKey = readonly [typeof QUERY.GET_GITHUB_TAGS, string];

export const useGithubRepositoriesQuery = () => {
  return useQuery<GithubRepository[], AxiosError<ErrorResponse>>(QUERY.GET_GITHUB_REPOSITORIES, () =>
    requestGetRepositories(getAccessToken())
  );
};

export const useGithubTagsQuery = (repositoryName: string) => {
  const isRepositoryNameNotEmpty = repositoryName !== "";

  const githubTagsQueryFunction: QueryFunction<string[]> = async ({ queryKey }) => {
    const [, repositoryName] = queryKey as GithubTagsQueryKey;

    return await requestGetTags(repositoryName, getAccessToken());
  };

  return useQuery<Tags, AxiosError<ErrorResponse>>([QUERY.GET_GITHUB_TAGS, repositoryName], githubTagsQueryFunction, {
    enabled: isRepositoryNameNotEmpty,
    cacheTime: 0,
  });
};
