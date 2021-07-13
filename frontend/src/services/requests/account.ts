import axios from "axios";
import { API_URL } from "../../constants/urls";

export const requestGetGithubAuthLink = async () => {
  const response = await axios.get<{ url: string }>(API_URL.AUTH.GITHUB);

  return response.data.url;
};

export const requestGetAccessToken = async (authCode: string) => {
  const response = await axios.get<{ username: string; token: string }>(API_URL.AFTER_LOGIN(authCode));

  return response.data;
};
