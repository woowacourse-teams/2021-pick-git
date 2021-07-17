import { useQuery } from "react-query";
import { GithubRepository, Tags } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestGetRepositories, requestGetTags } from "../requests/github";

export const useGithubRepositoriesQuery = (userName: string) => {
  const { accessToken } = useLocalStorage();
  const isUserNameEmpty = false;

  return useQuery<GithubRepository[]>(
    QUERY.GET_GITHUB_REPOSITORIES,
    () => requestGetRepositories(userName, accessToken),
    { enabled: true }
  );
};

export const useGithubTagsQuery = (repositoryName: string) => {
  const { accessToken } = useLocalStorage();
  const isRepositoryNameEmpty = repositoryName !== "";

  return useQuery<Tags>(QUERY.GET_GITHUB_TAGS, () => requestGetTags(repositoryName, accessToken), {
    enabled: true,
  });
};
