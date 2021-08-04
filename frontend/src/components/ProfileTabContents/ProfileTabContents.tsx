import useGithubStatistics from "../../services/hooks/useGithubStatistics";
import useUserFeed from "../../services/hooks/useUserFeed";
import GithubStatistics from "../GithubStatistics/GithubStatistics";
import ProfileFeed from "../ProfileFeed/ProfileFeed";

export interface Props {
  isMyProfile: boolean;
  username: string;
  tabIndex: number;
}

const ProfileTabContents = ({ isMyProfile, username, tabIndex }: Props) => {
  const userFeedProps = useUserFeed(isMyProfile, username);
  const githubStatisticQueryResult = useGithubStatistics(username);

  const tabContents = [
    <ProfileFeed key="profile-feed" username={username} {...userFeedProps} />,
    <GithubStatistics key="github-stats" username={username} githubStatisticQueryResult={githubStatisticQueryResult} />,
  ];

  return tabContents[tabIndex];
};

export default ProfileTabContents;
