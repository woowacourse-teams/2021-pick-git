import { InfiniteData } from "react-query";
import { Link } from "react-router-dom";
import { Post } from "../../../@types";
import { getItemsFromPages } from "../../../utils/infiniteData";

import InfiniteScrollContainer from "../InfiniteScrollContainer/InfiniteScrollContainer";
import NotFound from "../NotFound/NotFound";
import PageError from "../PageError/PageError";
import { Container, NotFoundCSS, Grid, GridItem } from "./GridFeed.styled";

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
    return <PageError errorMessage="피드를 가져올 수 없습니다" />;
  }

  const posts = getItemsFromPages<Post>(infinitePostsData.pages);

  if (posts && posts.length === 0) {
    return <NotFound type="post" message="게시글이 업로드되지 않았습니다." cssProp={NotFoundCSS} />;
  }

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
};

export default GridFeed;
