import axios from "axios";

import { Profile } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetSelfProfile = async (accessToken: string) => {
  const response = await axios.get<Profile>(API_URL.SELF_PROFILE, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data ?? {};
};
