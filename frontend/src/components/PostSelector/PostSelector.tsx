import { InfiniteData } from "react-query";
import { PortfolioProject, Post } from "../../@types";
import { getItemsFromPages } from "../../utils/infiniteData";
import { Container, Grid, GridItem, NotFoundCSS } from "./PostSelector.style";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import NotFound from "../@shared/NotFound/NotFound";
import { NOT_FOUND_MESSAGE } from "../../constants/messages";

export interface Props {
  infinitePostsData: InfiniteData<Post[]> | undefined;
  isFetchingNextPage: boolean;
  onIntersect: () => void;
  onPostSelect: (post: Post) => void;
}

const PostSelector = ({ infinitePostsData, isFetchingNextPage, onPostSelect, onIntersect }: Props) => {
  if (!infinitePostsData) {
    return <NotFound type="post" message={NOT_FOUND_MESSAGE.POSTS.NETWORK} cssProp={NotFoundCSS} />;
  }

  const posts = getItemsFromPages(infinitePostsData.pages);

  if (!posts || posts.length === 0) {
    return <NotFound type="post" message={NOT_FOUND_MESSAGE.POSTS.PROJECT} cssProp={NotFoundCSS} />;
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={onIntersect}>
        <Grid>
          {posts?.map((post) => (
            <GridItem key={post.id} imageUrl={post.imageUrls[0]} onClick={() => onPostSelect(post)}></GridItem>
          ))}
        </Grid>
      </InfiniteScrollContainer>
    </Container>
  );
};

export default PostSelector;
