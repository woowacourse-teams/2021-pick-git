import { AxiosError } from "axios";
import { useContext } from "react";
import { InfiniteData, QueryObserverResult, RefetchOptions } from "react-query";

import { ErrorResponse, SearchResult, SearchResultUser } from "../../@types";
import UserContext from "../../contexts/UserContext";
import useFollow from "../../services/hooks/useFollow";
import Avatar from "../@shared/Avatar/Avatar";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { Button, Empty, NameTag, UserList, ButtonLoader, ButtonSpinner } from "./SearchListUser.style";

export interface Props {
  isFetchingNextPage: boolean;
  onIntersect: () => void;
  users: SearchResultUser[];
  follow: ReturnType<typeof useFollow>;
  refetch: (
    options?: RefetchOptions | undefined
  ) => Promise<QueryObserverResult<InfiniteData<SearchResult | null>, AxiosError<ErrorResponse>>>;
}

const SearchListUser = ({ isFetchingNextPage, onIntersect, users, follow, refetch }: Props) => {
  const { isLoggedIn } = useContext(UserContext);
  const { toggleFollow, isFollowLoading, isUnfollowLoading } = follow;

  const handleFollowButtonClick = async (username: string, following: boolean) => {
    await toggleFollow(username, following);
    refetch();
  };

  const FollowButton = ({ username, following }: { username: string; following: boolean | null }) => {
    if (isLoggedIn && following !== null) {
      return following ? (
        <Button type="button" follow={true} onClick={() => handleFollowButtonClick(username, following)}>
          팔로우 취소
        </Button>
      ) : (
        <Button type="button" follow={false} onClick={() => handleFollowButtonClick(username, following)}>
          팔로우
        </Button>
      );
    }

    return <></>;
  };

  if (isFollowLoading || isUnfollowLoading) {
    return (
      <ButtonLoader type="button" follow={isFollowLoading}>
        {isFollowLoading ? "팔로우" : "팔로우 취소"}
        <ButtonSpinner size="1rem" />
      </ButtonLoader>
    );
  }

  if (!users.length) {
    return <Empty>검색결과가 없습니다.</Empty>;
  }

  return (
    <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={onIntersect}>
      <ul>
        {users.map((user) => (
          <UserList key={user.username}>
            <NameTag>
              <Avatar diameter="1.875rem" imageUrl={user.imageUrl} />
              <span>{user.username}</span>
            </NameTag>
            <FollowButton username={user.username} following={user.following} />
          </UserList>
        ))}
      </ul>
    </InfiniteScrollContainer>
  );
};

export default SearchListUser;
