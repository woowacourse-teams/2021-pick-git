import { useContext } from "react";
import { ThemeContext } from "styled-components";

import { SearchResultUser } from "../../@types";
import UserContext from "../../contexts/UserContext";
import Avatar from "../@shared/Avatar/Avatar";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { Button, Empty, NameTag, UserList } from "./SearchListUser.style";

export interface Props {
  isFetchingNextPage: boolean;
  onIntersect: () => void;
  users: SearchResultUser[];
}

const SearchListUser = ({ isFetchingNextPage, onIntersect, users }: Props) => {
  const { isLoggedIn } = useContext(UserContext);

  const FollowButton = ({ following }: { following: boolean | null }) => {
    if (!isLoggedIn || following === null) {
      return <></>;
    }

    if (following) {
      return (
        <Button type="button" follow={false}>
          팔로우
        </Button>
      );
    } else {
      return (
        <Button type="button" follow={true}>
          팔로우 취소
        </Button>
      );
    }
  };
  console.log("search list", users);
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
            <FollowButton following={user.following} />
          </UserList>
        ))}
      </ul>
    </InfiniteScrollContainer>
  );
};

export default SearchListUser;
