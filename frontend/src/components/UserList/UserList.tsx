import { useContext, useState } from "react";
import { InfiniteData, QueryKey, useQueryClient } from "react-query";

import { UserItem } from "../../@types";
import { WARNING_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import useModal from "../../hooks/common/useModal";
import useFollow from "../../hooks/service/useFollow";
import { isInfiniteData } from "../../utils/typeGuard";
import ChoiceModalPortal from "../@layout/ChoiceModalPortal/ChoiceModalPortal";
import Avatar from "../@shared/Avatar/Avatar";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { Button, Empty, NameTag, ListItem } from "./UserList.style";

export interface Props {
  isFetchingNextPage: boolean;
  onIntersect: () => void;
  users: UserItem[];
  queryKey: QueryKey;
}

const findCurrentUser = (pages: UserItem[][], username: string): { pageIndex: number; itemIndex: number } | null => {
  for (let pageIndex = 0; pageIndex < pages.length; pageIndex++) {
    for (let itemIndex = 0; itemIndex < pages[pageIndex].length; itemIndex++) {
      if (pages[pageIndex][itemIndex].username === username) {
        return { pageIndex, itemIndex };
      }
    }
  }

  return null;
};

const UserList = ({ isFetchingNextPage, onIntersect, users, queryKey }: Props) => {
  const [currentUsername, setCurrentUsername] = useState("");
  const [currentUserFollowing, setCurrentUserFollowing] = useState(false);
  const { isLoggedIn } = useContext(UserContext);
  const queryClient = useQueryClient();
  const {
    isModalShown: isChoiceModalShown,
    modalMessage: choiceModalMessage,
    showModal: showChoiceModal,
    hideModal: hideChoiceModal,
  } = useModal();

  const setInfiniteUserListQueryData = (currentQueryData: InfiniteData<UserItem[]>, following: boolean) => {
    const currentUserLocation = findCurrentUser(currentQueryData?.pages ?? [], currentUsername);

    if (currentQueryData && currentUserLocation) {
      const { pageParams, pages } = currentQueryData;
      const { pageIndex, itemIndex } = currentUserLocation;

      queryClient.setQueryData<InfiniteData<UserItem[]>>(queryKey, {
        pageParams: [...pageParams],
        pages: [
          ...pages.slice(0, pageIndex),
          [
            ...pages[pageIndex].slice(0, itemIndex),
            { ...pages[pageIndex][itemIndex], following },
            ...pages[pageIndex].slice(itemIndex + 1),
          ],
          ...pages.slice(pageIndex + 1),
        ],
      });
    }
  };

  const setFiniteUserListQueryData = (currentQueryData: UserItem[], following: boolean) => {
    const currentUserIndex = currentQueryData.findIndex((item) => item.username === currentUsername);

    if (currentUserIndex > -1) {
      queryClient.setQueryData<UserItem[]>(queryKey, [
        ...currentQueryData.slice(0, currentUserIndex),
        { ...currentQueryData[currentUserIndex], following },
        ...currentQueryData.slice(currentUserIndex + 1),
      ]);
    }
  };

  const setUserListQueryData = (following: boolean) => {
    const currentQueryData = queryClient.getQueryData<InfiniteData<UserItem[]> | UserItem[]>(queryKey);

    if (isInfiniteData(currentQueryData)) {
      setInfiniteUserListQueryData(currentQueryData, following);
    } else {
      setFiniteUserListQueryData(currentQueryData ?? [], following);
    }
  };

  const { toggleFollow } = useFollow(setUserListQueryData);

  const toggleFollowWithGithubFollowing = (applyGithub: boolean) => () => {
    hideChoiceModal();
    toggleFollow(currentUsername, currentUserFollowing, applyGithub);
  };

  const handleFollowButtonClick = (username: string, following: boolean) => {
    setCurrentUsername(username);
    setCurrentUserFollowing(following);
    showChoiceModal(following ? WARNING_MESSAGE.GITHUB_UNFOLLOWING : WARNING_MESSAGE.GITHUB_FOLLOWING);
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

  if (!users.length) {
    return <Empty>일치하는 계정이 없습니다.</Empty>;
  }

  return (
    <>
      <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={onIntersect}>
        <ul>
          {users.map((user) => (
            <ListItem key={user.username}>
              <NameTag to={PAGE_URL.USER_PROFILE(user.username)}>
                <Avatar diameter="1.875rem" imageUrl={user.imageUrl} />
                <span>{user.username}</span>
              </NameTag>
              <FollowButton username={user.username} following={user.following} />
            </ListItem>
          ))}
        </ul>
      </InfiniteScrollContainer>
      {isChoiceModalShown && (
        <ChoiceModalPortal
          heading={choiceModalMessage}
          onPositiveChoose={toggleFollowWithGithubFollowing(true)}
          onNagativeChoose={toggleFollowWithGithubFollowing(false)}
          onClose={hideChoiceModal}
          positiveChoiceText="예"
          nagativeChoiceText="아니오"
        />
      )}
    </>
  );
};

export default UserList;
