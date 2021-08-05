import { AxiosError } from "axios";
import { useContext, useState } from "react";
import { InfiniteData, QueryObserverResult, RefetchOptions } from "react-query";

import { ErrorResponse, SearchResultUser } from "../../@types";
import { WARNING_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import useFollow from "../../services/hooks/useFollow";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
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
  ) => Promise<QueryObserverResult<InfiniteData<SearchResultUser[] | null>, AxiosError<ErrorResponse>>>;
}

const SearchListUser = ({ isFetchingNextPage, onIntersect, users, follow, refetch }: Props) => {
  const [currentUsername, setCurrentUsername] = useState("");
  const [currentUserFollowing, setCurrentUserFollowing] = useState(false);
  const { isLoggedIn } = useContext(UserContext);
  const { modalMessage, isModalShown: isMessageModalShown, hideMessageModal, showConfirmModal } = useMessageModal();
  const { toggleFollow, isFollowLoading, isUnfollowLoading } = follow;

  const toggleFollowWithGithubFollowing = (applyGithub: boolean) => async () => {
    await toggleFollow(currentUsername, currentUserFollowing, applyGithub);
    refetch();
  };

  const handleFollowButtonClick = (username: string, following: boolean) => {
    setCurrentUsername(username);
    setCurrentUserFollowing(following);
    showConfirmModal(following ? WARNING_MESSAGE.GITHUB_UNFOLLOWING : WARNING_MESSAGE.GITHUB_FOLLOWING);
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

    if (isFollowLoading || isUnfollowLoading) {
      return (
        <ButtonLoader type="button" follow={isFollowLoading}>
          {isFollowLoading ? "팔로우" : "팔로우 취소"}
          <ButtonSpinner size="1rem" />
        </ButtonLoader>
      );
    }

    return <></>;
  };

  if (!users.length) {
    return <Empty>검색결과가 없습니다.</Empty>;
  }

  return (
    <>
      <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={onIntersect}>
        <ul>
          {users.map((user) => (
            <UserList key={user.username}>
              <NameTag to={PAGE_URL.USER_PROFILE(user.username)}>
                <Avatar diameter="1.875rem" imageUrl={user.imageUrl} />
                <span>{user.username}</span>
              </NameTag>
              <FollowButton username={user.username} following={user.following} />
            </UserList>
          ))}
        </ul>
      </InfiniteScrollContainer>
      {isMessageModalShown && (
        <MessageModalPortal
          heading={modalMessage}
          onConfirm={toggleFollowWithGithubFollowing(true)}
          onCancel={toggleFollowWithGithubFollowing(false)}
          onClose={hideMessageModal}
        />
      )}
    </>
  );
};

export default SearchListUser;
