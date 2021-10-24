import { PAGE_URL } from "../../constants/urls";
import useGithubStatistics from "../../hooks/service/useGithubStatistics";
import useUserFeed from "../../hooks/service/useUserFeed";
import GithubStatistics from "../GithubStatistics/GithubStatistics";
import GridFeed from "../@shared/GridFeed/GridFeed";
import { useEffect } from "react";

export interface Props {
  isMyProfile: boolean;
  username: string;
  tabIndex: number;
}

const ProfileTabContents = ({ isMyProfile, username, tabIndex }: Props) => {
  const isGithubStatsFocused = tabIndex === 1;
  const userFeedProps = useUserFeed(isMyProfile, username);
  const githubStatisticQueryResult = useGithubStatistics(username, isGithubStatsFocused);

  useEffect(() => {
    if (isGithubStatsFocused) {
      githubStatisticQueryResult.refetch();
    }
  }, [isGithubStatsFocused]);

  const tabContents = [
    <GridFeed key="profile-feed" feedPagePath={PAGE_URL.USER_FEED(username)} {...userFeedProps} />,
    <GithubStatistics
      key="github-stats"
      username={username}
      githubStatisticQueryResult={githubStatisticQueryResult}
      isFocused={isGithubStatsFocused}
    />,
  ];

  return tabContents[tabIndex];
};

export default ProfileTabContents;
