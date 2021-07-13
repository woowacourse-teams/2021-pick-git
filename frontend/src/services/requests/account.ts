import axios from "axios";
import { API_URL } from "../../constants/urls";

export const requestGetGithubAuthLink = async () => {
  const response = await axios.get<{ url: string }>(API_URL.AUTH.GITHUB);

  return response.data.url;
};
