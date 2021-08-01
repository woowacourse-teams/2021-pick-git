import { Link } from "react-router-dom";
import { PAGE_URL } from "../../constants/urls";
import useUserFeed from "../../services/hooks/useUserFeed";
import { getPostsFromPages } from "../../utils/feed";

import PageLoading from "../@layout/PageLoading/PageLoading";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { Container, Empty, Grid, GridItem } from "./ProfileFeed.styled";

export interface Props extends ReturnType<typeof useUserFeed> {
  username: string;
}

const ProfileFeed = ({
  username,
  infinitePostsData,
  isLoading,
  isError,
  isFetchingNextPage,
  handleIntersect,
}: Props) => {
  if (isLoading) {
    return (
      <Empty>
        <PageLoading />
      </Empty>
    );
  }

  if (isError || !infinitePostsData) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  const posts = getPostsFromPages(infinitePostsData.pages);

  const Feed = () => {
    if (posts.length > 0) {
      return (
        <Container>
          <InfiniteScrollContainer
            isLoaderShown={isFetchingNextPage ?? false}
            onIntersect={handleIntersect ?? (() => {})}
          >
            <Grid>
              {posts?.map(({ id, imageUrls, authorName, content }) => (
                <Link
                  to={{
                    pathname: PAGE_URL.USER_FEED,
                    search: `?username=${username}`,
                    state: { prevData: infinitePostsData, postId: id },
                  }}
                  key={id}
                >
                  <GridItem imageUrl={imageUrls[0]} aria-label={`${authorName}님의 게시물. ${content}`} />
                </Link>
              ))}
            </Grid>
          </InfiniteScrollContainer>
        </Container>
      );
    } else {
      return <Empty>게시물이 없습니다.</Empty>;
    }
  };

  return <Feed />;
};

export default ProfileFeed;
