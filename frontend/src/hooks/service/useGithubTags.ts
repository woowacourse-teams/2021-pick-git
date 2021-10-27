import { useGithubTagsQuery } from "../../services/queries";

const useGithubTags = (githubRepositoryName: string) => useGithubTagsQuery(githubRepositoryName);

export default useGithubTags;
