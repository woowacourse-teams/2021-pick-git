import axios, { AxiosError } from "axios";
import { useContext } from "react";
import { Link, useHistory } from "react-router-dom";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { useUserPostsQuery } from "../../services/queries";
import { Empty, GridContainer, GridItem } from "./ProfileFeed.styled";

export interface Props {
  isMyFeed: boolean;
  username: string | null;
}

const ProfileFeed = ({ isMyFeed, username }: Props) => {
  const history = useHistory();
  const { isLoggedIn, logout } = useContext(UserContext);
  const { pushMessage } = useContext(SnackBarContext);
  const { data, isLoading, error, refetch } = useUserPostsQuery(isMyFeed, username);

  const handleAxiosError = (error: AxiosError) => {
    const { status } = error.response ?? {};

    if (status === 401) {
      if (isMyFeed) {
        pushMessage("로그인한 사용자만 사용할 수 있는 서비스입니다.");

        history.push(PAGE_URL.HOME);
      } else {
        isLoggedIn && pushMessage("사용자 정보가 유효하지 않아 자동으로 로그아웃합니다.");
        logout();
        refetch();
      }
    }
  };

  if (isLoading) {
    return <div>loading</div>;
  }

  if (error) {
    if (axios.isAxiosError(error)) {
      handleAxiosError(error);
    }

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
