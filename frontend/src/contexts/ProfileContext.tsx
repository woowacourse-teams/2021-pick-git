import { createContext, useContext } from "react";
import { UseQueryResult } from "react-query";
import { useLocation } from "react-router-dom";
import { AxiosError } from "axios";

import { GithubStats } from "../@types";
import useUserFeed from "../services/hooks/useUserFeed";
import { useGithubStatsQuery } from "../services/queries";
import UserContext from "./UserContext";
import useProfile from "../services/hooks/useProfile";

interface Props {
  children: React.ReactNode;
  isMyProfile: boolean;
}

interface Value {
  isMyProfile: boolean;
  username: string | null;
  userFeedProps: ReturnType<typeof useUserFeed>;
  githubStatisticsProps: UseQueryResult<GithubStats, AxiosError<GithubStats> | Error>;
  profileProps: ReturnType<typeof useProfile>;
}

const ProfileContext = createContext<Value | null>(null);

export const ProfileContextProvider = ({ children, isMyProfile }: Props) => {
  const username = new URLSearchParams(useLocation().search).get("username");
  const { currentUsername } = useContext(UserContext);
  const fixedUsername = isMyProfile ? currentUsername : username;

  const userFeedProps = useUserFeed(isMyProfile, fixedUsername);
  const githubStatisticsProps = useGithubStatsQuery(fixedUsername);
  const profileProps = useProfile(isMyProfile, fixedUsername);

  return (
    <ProfileContext.Provider
      value={{ isMyProfile, username: fixedUsername, userFeedProps, githubStatisticsProps, profileProps }}
    >
      {children}
    </ProfileContext.Provider>
  );
};

export default ProfileContext;
