import { PAGE_URL } from "../../constants/urls";
import useGithubStatistics from "../../services/hooks/useGithubStatistics";
import useUserFeed from "../../services/hooks/useUserFeed";
import GithubStatistics from "../GithubStatistics/GithubStatistics";
import GridFeed from "../@shared/GridFeed/GridFeed";

export interface Props {
  isMyProfile: boolean;
  username: string;
  tabIndex: number;
}

const ProfileTabContents = ({ isMyProfile, username, tabIndex }: Props) => {
  const userFeedProps = useUserFeed(isMyProfile, username);
  const githubStatisticQueryResult = useGithubStatistics(username);

  const tabContents = [
    <GridFeed key="profile-feed" feedPagePath={PAGE_URL.USER_FEED(username)} {...userFeedProps} />,
    <GithubStatistics key="github-stats" username={username} githubStatisticQueryResult={githubStatisticQueryResult} />,
  ];

  return tabContents[tabIndex];
};

export default ProfileTabContents;
