import { useQuery } from "react-query";
import { GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

export const useGithubRepositoriesQuery = () => {
  return useQuery<GithubRepository[]>(QUERY.GET_GITHUB_REPOSITORIES, () => requestGetRepositories(getAccessToken()), {
    cacheTime: 0,
  });
};

export const useGithubTagsQuery = (repositoryName: string) => {
  const isRepositoryNameNotEmpty = repositoryName !== "";

  return useQuery<Tags>(QUERY.GET_GITHUB_TAGS, () => requestGetTags(repositoryName, getAccessToken()), {
    enabled: isRepositoryNameNotEmpty,
    cacheTime: 0,
  });
};
