import { useEffect } from "react";
import { Post } from "../../@types";
import { NOT_FOUND_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import useGithubStatistics from "../../hooks/service/useGithubStatistics";
import useUserFeed from "../../hooks/service/useUserFeed";
import { getItemsFromPages } from "../../utils/infiniteData";
import GridFeed from "../@shared/GridFeed/GridFeed";
import NotFound from "../@shared/NotFound/NotFound";
import GithubStatistics from "../GithubStatistics/GithubStatistics";
import { NotFoundCSS } from "./ProfileTabContents.style";

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

  const posts = getItemsFromPages<Post>(userFeedProps.infinitePostsData?.pages);

  const tabContents = [
    posts && posts.length !== 0 ? (
      <GridFeed key="profile-feed" feedPagePath={PAGE_URL.USER_FEED(username)} {...userFeedProps} />
    ) : (
      <NotFound type="post" message={NOT_FOUND_MESSAGE.POSTS.DEFAULT} cssProp={NotFoundCSS} />
    ),
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
