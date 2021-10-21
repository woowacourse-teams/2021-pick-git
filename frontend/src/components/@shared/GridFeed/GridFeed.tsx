import { InfiniteData } from "react-query";
import { Link } from "react-router-dom";
import { Post } from "../../../@types";
import { getItemsFromPages } from "../../../utils/infiniteData";

import InfiniteScrollContainer from "../InfiniteScrollContainer/InfiniteScrollContainer";
import { Container, Empty, Grid, GridItem } from "./GridFeed.styled";

export interface Props {
  feedPagePath?: string;
  infinitePostsData?: InfiniteData<Post[] | null>;
  isLoading: boolean;
  isError: boolean;
  isFetchingNextPage: boolean;
  handleIntersect: () => void;
}

const GridFeed = ({ feedPagePath, infinitePostsData, isError, isFetchingNextPage, handleIntersect }: Props) => {
  if (isError || !infinitePostsData) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  const posts = getItemsFromPages<Post>(infinitePostsData.pages);

  const Feed = () => {
    if (posts?.length ?? 0 > 0) {
      return (
        <Container>
          <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={handleIntersect}>
            <Grid>
              {posts?.map(({ id, imageUrls, authorName, content }) => (
                <Link
                  to={{
                    pathname: feedPagePath?.split("?")[0] ?? "",
                    search: `?${feedPagePath?.split("?")[1]}`,
                    state: { postId: id },
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

export default GridFeed;
