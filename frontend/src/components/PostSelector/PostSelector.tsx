import { InfiniteData } from "react-query";
import { PortfolioProject, Post } from "../../@types";
import { getItemsFromPages } from "../../utils/infiniteData";
import { Container, Grid, GridItem } from "./PostSelector.style";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";

export interface Props {
  infinitePostsData: InfiniteData<Post[]> | undefined;
  isFetchingNextPage: boolean;
  onIntersect: () => void;
  onPostSelect: (post: Post) => void;
}

const PostSelector = ({ infinitePostsData, isFetchingNextPage, onPostSelect, onIntersect }: Props) => {
  if (!infinitePostsData) {
    return <div>게시물 정보를 가져올 수 없습니다.</div>;
  }

  const posts = getItemsFromPages(infinitePostsData.pages);

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
