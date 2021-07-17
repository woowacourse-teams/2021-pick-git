import axios from "axios";
import { GithubRepository, Tags } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetRepositories = async (userName: string, accessToken: string | null) => {
  if (!accessToken) {
    throw Error("no accessToken");
  }

  const response = await axios.get<GithubRepository[]>(API_URL.GITHUB_REPOSITORIES(userName), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetTags = async (repositoryName: string, accessToken: string | null) => {
  if (!accessToken) {
    throw Error("no accessToken");
  }

  const response = await axios.get<Tags>(API_URL.GITHUB_TAGS(repositoryName), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};
