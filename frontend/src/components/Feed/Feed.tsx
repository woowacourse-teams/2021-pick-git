import axios from "axios";
import { useContext } from "react";

import UserContext from "../../contexts/UserContext";
import { useState } from "react";
import { Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/message";
import useFeed from "../../services/hooks/useFeed";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PostItem from "../@shared/PostItem/PostItem";
import { Container, PostItemWrapper } from "./Feed.style";
import { UseInfiniteQueryResult, useQueryClient } from "react-query";
import { QUERY } from "../../constants/queries";

const Feed = ({ queryResult }: { queryResult: UseInfiniteQueryResult<Post[], unknown> }) => {
  const { logout } = useContext(UserContext);
  const queryClient = useQueryClient();
  const { data, isLoading, error, fetchNextPage } = queryResult;
  const { commentValue, setCommentValue, deletePostLike, addPostLike, setPosts, addComment } = useFeed();

  const allPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []);

  const handleCommentValueChange: React.ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => {
    if (value.length > LIMIT.COMMENT_LENGTH) {
      alert(FAILURE_MESSAGE.COMMENT_CONTENT_MAX_LENGTH_EXCEEDED);
      return;
    }

    setCommentValue(value);
  };

  const handleCommentValueSave = (postId: Post["postId"]) => {
    addComment(postId, commentValue);
  };

  const handleCommentLike = (commentId: string) => {
    alert("아직 구현되지 않은 기능입니다.");
  };

  const handlePostLike = (postId: string) => {
    if (!allPosts) {
      return;
    }

    const newPosts = [...allPosts];
    const targetPost = newPosts.find((post) => post.postId === postId);

    if (!targetPost) {
      return;
    }

    if (targetPost.isLiked) {
      deletePostLike(targetPost);
      targetPost.isLiked = false;
      setPosts(newPosts);
      return;
    }

    if (!targetPost.isLiked) {
      addPostLike(targetPost);
      targetPost.isLiked = true;
      setPosts(newPosts);
    }
  };

  const handlePostsEndIntersect = () => {
    fetchNextPage();
    // setPageIndex((state) => state + 1);
  };

  if (error) {
    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status === 401) {
        logout();
        queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS, { active: true });
      }
    }

    return <div>에러!!</div>;
  }

  if (isLoading) {
    return <div>로딩!!</div>;
  }

  return (
    <Container>
      <InfiniteScrollContainer onIntersect={handlePostsEndIntersect}>
        {allPosts?.map((post) => (
          <PostItemWrapper key={post.postId}>
            <PostItem
              authorName={post.authorName}
              authorGithubUrl={post.githubRepoUrl}
              authorImageUrl={post.profileImageUrl}
              imageUrls={post.imageUrls}
              commenterImageUrl={
                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80"
              }
              createdAt={post.createdAt}
              comments={post.comments}
              content={post.content}
              isEditable={true}
              isLiked={post.isLiked}
              likeCount={post.likesCount}
              tags={post.tags}
              commentValue={commentValue}
              onCommentValueChange={handleCommentValueChange}
              onCommentValueSave={() => handleCommentValueSave(post.postId)}
              onCommentLike={handleCommentLike}
              onPostLike={() => handlePostLike(post.postId)}
            />
          </PostItemWrapper>
        ))}
      </InfiniteScrollContainer>
    </Container>
  );
};

export default Feed;
