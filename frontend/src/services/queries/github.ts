import { useQuery } from "react-query";
import { GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

export const useGithubRepositoriesQuery = (username: string) => {
  const { accessToken } = useLocalStorage();
  const isUserNotNameEmpty = username !== "";

  return useQuery<GithubRepository[]>(
    QUERY.GET_GITHUB_REPOSITORIES,
    () => requestGetRepositories(username, accessToken),
    { enabled: isUserNotNameEmpty }
  );
};

export const useGithubTagsQuery = (username: string, repositoryName: string) => {
  const { accessToken } = useLocalStorage();
  const isRepositoryNameNotEmpty = repositoryName !== "";

  return useQuery<Tags>(QUERY.GET_GITHUB_TAGS, () => requestGetTags(username, repositoryName, accessToken), {
    enabled: isRepositoryNameNotEmpty,
  });
};
