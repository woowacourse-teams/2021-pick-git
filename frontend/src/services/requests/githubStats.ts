import axios from "axios";

export const requestGetGithubStats = async (username: string) => {
  const response = await axios.get(`https://parse-github-stats.herokuapp.com/github-stats/${username}`);

  return response.data;
};
