import { useContext } from "react";
import { Link } from "react-router-dom";

import ProfileContext from "../../contexts/ProfileContext";
import PageLoading from "../@layout/PageLoading/PageLoading";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { Container, Empty, Grid, GridItem } from "./ProfileFeed.styled";

const ProfileFeed = () => {
  const { userFeedProps } = useContext(ProfileContext) ?? {};
  const { allPosts, isLoading, isError, isFetchingNextPage, handleIntersect } = userFeedProps ?? {};

  if (isLoading) {
    return (
      <Empty>
        <PageLoading />
      </Empty>
    );
  }

  if (isError) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  const Feed = () => {
    if (allPosts?.length) {
      return (
        <Container>
          <InfiniteScrollContainer
            isLoaderShown={isFetchingNextPage ?? false}
            onIntersect={handleIntersect ?? (() => {})}
          >
            <Grid>
              {allPosts?.map(({ id, imageUrls, authorName, content }) => (
                <Link to="" key={id}>
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
