import { Link } from "react-router-dom";
import { useUserPostsQuery } from "../../services/queries";
import { Empty, GridContainer, GridItem } from "./ProfileFeed.styled";

export interface Props {
  isMyFeed: boolean;
  username: string | null;
}

const ProfileFeed = ({ isMyFeed, username }: Props) => {
  const { data, isLoading, error } = useUserPostsQuery(isMyFeed, username);

  if (isLoading) {
    return <div>loading</div>;
  }

  if (error) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  const Feed = () => {
    if (data?.length) {
      return (
        <GridContainer>
          {data?.map(({ postId, imageUrls, authorName, content }) => (
            <Link to="" key={postId}>
              <GridItem imageUrl={imageUrls[0]} aria-label={`${authorName}님의 게시물. ${content}`} />
            </Link>
          ))}
        </GridContainer>
      );
    } else {
      return <Empty>게시물이 없습니다.</Empty>;
    }
  };

  return <Feed />;
};

export default ProfileFeed;
