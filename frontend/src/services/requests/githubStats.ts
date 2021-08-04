import axios from "axios";
import { GithubStats } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetGithubStats = async (username: string, accessToken: string | null) => {
  if (!accessToken) {
    return null;
  }

  const response = await axios.get<GithubStats>(API_URL.GITHUB_STATS(username), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};
