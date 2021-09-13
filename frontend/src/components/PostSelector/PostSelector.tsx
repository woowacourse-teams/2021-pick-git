import { InfiniteData } from "react-query";
import { PortfolioProject, Post } from "../../@types";
import { getItemsFromPages } from "../../utils/infiniteData";
import { Container, Grid, GridItem } from "./PostSelector.style";

export interface Props {
  infinitePostsData: InfiniteData<Post[]> | undefined;
  onPostSelect: (post: Post) => void;
}

const PostSelector = ({ onPostSelect, infinitePostsData }: Props) => {
  if (!infinitePostsData) {
    return <div>게시물 정보를 가져올 수 없습니다.</div>;
  }

  const posts = getItemsFromPages(infinitePostsData.pages);

  return (
    <Container>
      <Grid>
        {posts.map((post) => (
          <GridItem imageUrl={post.imageUrls[0]} onClick={() => onPostSelect(post)}></GridItem>
        ))}
      </Grid>
    </Container>
  );
};

export default PostSelector;
