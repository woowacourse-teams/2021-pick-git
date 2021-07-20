import { useQuery } from "react-query";
import { GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

export const useGithubRepositoriesQuery = (username: string) => {
  const isUserNotNameEmpty = username !== "";

  return useQuery<GithubRepository[]>(
    QUERY.GET_GITHUB_REPOSITORIES,
    () => requestGetRepositories(username, getAccessToken()),
    { enabled: isUserNotNameEmpty, cacheTime: 0 }
  );
};

export const useGithubTagsQuery = (username: string, repositoryName: string) => {
  const isRepositoryNameNotEmpty = repositoryName !== "";

  return useQuery<Tags>(QUERY.GET_GITHUB_TAGS, () => requestGetTags(username, repositoryName, getAccessToken()), {
    enabled: isRepositoryNameNotEmpty,
    cacheTime: 0,
  });
};
