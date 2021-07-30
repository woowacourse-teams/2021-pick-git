import { AxiosError } from "axios";
import { useQuery } from "react-query";
import { ErrorResponse, GithubStats } from "../../@types";
import { QUERY } from "../../constants/queries";
import { requestGetGithubStats } from "../requests";

export const useGithubStatsQuery = (username: string) => {
  return useQuery<GithubStats, AxiosError<ErrorResponse>>([QUERY.GET_GITHUB_STATS, username], () =>
    requestGetGithubStats(username)
  );
};
