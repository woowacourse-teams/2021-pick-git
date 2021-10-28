import axios from "axios";
import { GithubRepository, Tags } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";
import { customError } from "../../utils/error";

export const requestGetRepositories = async (keyword: string, pageParam: number, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.get<GithubRepository[]>(
    API_URL.GITHUB_REPOSITORIES(keyword, pageParam, LIMIT.REPOSITORIES_COUNT_PER_FETCH),
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  return response.data;
};

export const requestGetTags = async (repositoryName: string, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  if (repositoryName === "") {
    return [];
  }

  const response = await axios.get<Tags>(API_URL.GITHUB_TAGS(repositoryName), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};
