import { useQuery } from "react-query";

import { Profile } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestGetSelfProfile } from "../requests";

export const useSelfProfileQuery = () => {
  const { accessToken } = useLocalStorage();

  if (!accessToken) {
    throw Error("on accessToken");
  }

  return useQuery<Profile>(QUERY.GET_SELF_PROFILE, () => requestGetSelfProfile(accessToken));
};
