import { useQuery } from "react-query";
import { GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

export const useGithubRepositoriesQuery = (userName: string) => {
  const { accessToken } = useLocalStorage();
  const isUserNotNameEmpty = userName !== "";

  return useQuery<GithubRepository[]>(
    QUERY.GET_GITHUB_REPOSITORIES,
    () => requestGetRepositories(userName, accessToken),
    { enabled: isUserNotNameEmpty }
  );
};

export const useGithubTagsQuery = (userName: string, repositoryName: string) => {
  const { accessToken } = useLocalStorage();
  const isRepositoryNameNotEmpty = repositoryName !== "";

  return useQuery<Tags>(QUERY.GET_GITHUB_TAGS, () => requestGetTags(userName, repositoryName, accessToken), {
    enabled: isRepositoryNameNotEmpty,
  });
};
