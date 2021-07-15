import { Link } from "react-router-dom";
import { useUserPostsQuery } from "../../services/queries";
import { GridContainer, GridItem } from "./ProfileFeed.styled";

let id = 0;

const dummyData = [...Array(10)].map(() => ({
  id: id++,
  authorName: "tanney",
  imageUrls: [
    "https://images.unsplash.com/photo-1518574095400-c75c9b094daa?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80",
  ],
  content: "내용",
}));

export interface Props {
  isMyFeed: boolean;
  userName?: string;
}

const ProfileFeed = ({ isMyFeed, userName }: Props) => {
  const { data, isLoading, error } = useUserPostsQuery(isMyFeed, userName);

  if (isLoading) {
    return <div>loading</div>;
  }

  if (error) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  return (
    <GridContainer>
      {data?.map(({ postId, imageUrls, authorName, content }) => (
        <Link to="" key={postId}>
          <GridItem imageUrl={imageUrls[0]} aria-label={`${authorName}님의 게시물. ${content}`} />
        </Link>
      ))}
    </GridContainer>
  );
};

export default ProfileFeed;
