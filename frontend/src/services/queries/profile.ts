import { QueryFunction, useQuery } from "react-query";
import { AxiosError } from "axios";

import { Profile } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestGetSelfProfile, requestGetUserProfile } from "../requests";

type ProfileQueryKey = readonly [
  typeof QUERY.GET_PROFILE,
  {
    isMyProfile: boolean;
    accessToken: string | null;
    userName?: string;
  }
];

const profileQueryFunction: QueryFunction<Profile> = async ({ queryKey }) => {
  const [, { isMyProfile, accessToken, userName }] = queryKey as ProfileQueryKey;

  if (isMyProfile && !accessToken) {
    throw Error("no accessToken");
  }

  if (isMyProfile) {
    if (!accessToken) throw Error("no accessToken");

    return await requestGetSelfProfile(accessToken);
  } else {
    return await requestGetUserProfile(userName as string, accessToken);
  }
};

export const useProfileQuery = (isMyProfile: boolean, userName: string) => {
  const { accessToken } = useLocalStorage();

  return useQuery<Profile, AxiosError<Profile>>(
    [QUERY.GET_PROFILE, { isMyProfile, accessToken, userName }],
    profileQueryFunction
  );
};
