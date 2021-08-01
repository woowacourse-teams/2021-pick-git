import axios from "axios";
import { GithubStats } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetGithubStats = async (username: string) => {
  // const response = await axios.get<GithubStats>(API_URL.GITHUB_STATS(username));
  const response = await axios.get<GithubStats>(`https://parse-github-stats.herokuapp.com/github-stats/${username}`);

  return response.data;
};
