import { AxiosError } from "axios";
import { useQuery } from "react-query";
import { ErrorResponse, GithubStats } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetGithubStats } from "../requests";

export const useGithubStatsQuery = (username: string) => {
  return useQuery<GithubStats | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_GITHUB_STATS, username],
    () => requestGetGithubStats(username, getAccessToken()),
    { suspense: true }
  );
};
