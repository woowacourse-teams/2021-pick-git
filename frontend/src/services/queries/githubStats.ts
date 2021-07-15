import { AxiosError } from "axios";
import { QueryFunction, useQuery } from "react-query";
import { GithubStats } from "../../@types";
import { QUERY } from "../../constants/queries";
import { requestGetGithubStats } from "../requests";

type GithubStatsQueryKey = readonly [typeof QUERY.GET_GITHUB_STATS, string];

const githubStatsQueryFunction: QueryFunction<GithubStats> = async ({ queryKey }) => {
  const [, userName] = queryKey as GithubStatsQueryKey;

  return await requestGetGithubStats(userName);
};

export const useGithubStatsQuery = (userName: string) => {
  return useQuery<GithubStats, AxiosError<GithubStats>>([QUERY.GET_GITHUB_STATS, userName], githubStatsQueryFunction);
};
