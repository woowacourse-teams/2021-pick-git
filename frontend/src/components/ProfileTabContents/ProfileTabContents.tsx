import useUserFeed from "../../services/hooks/useUserFeed";
import { useGithubStatsQuery } from "../../services/queries";
import GithubStatistics from "../GithubStatistics/GithubStatistics";
import ProfileFeed from "../ProfileFeed/ProfileFeed";

export interface Props {
  isMyProfile: boolean;
  username: string | null;
  tabIndex: number;
}

const ProfileTabContents = ({ isMyProfile, username, tabIndex }: Props) => {
  const userFeedProps = useUserFeed(isMyProfile, username);
  const githubStatisticQueryResult = useGithubStatsQuery(username);

  const tabContents = [
    <ProfileFeed key="profile-feed" {...userFeedProps} />,
    <GithubStatistics key="github-stats" username={username} githubStatisticQueryResult={githubStatisticQueryResult} />,
  ];

  return tabContents[tabIndex];
};

export default ProfileTabContents;
