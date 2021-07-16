import axios from "axios";

export const requestGetGithubStats = async (userName: string) => {
  const response = await axios.get(`https://parse-github-stats.herokuapp.com/github-stats/${userName}`);

  return response.data;
};
