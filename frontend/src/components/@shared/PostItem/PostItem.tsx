import {
  Container,
  CommentPageMoveLink,
  CommentsWrapper,
  CommentWrapper,
  IconLink,
  IconLinkButtonsWrapper,
  LikeCountText,
  PostAuthorInfoLink,
  PostAuthorName,
  PostContentAuthorLink,
  PostBody,
  PostContent,
  PostHeader,
  TagListWrapper,
  TagItemLinkButton,
  PostCreatedDateText,
  MoreContentLinkButton,
  CommentPageMoveLinkText,
  MoreCommentExistIndicator,
  PostHeaderButtonsWrapper,
  ShareButtonDrawerCSS,
  ShareLinkCSS,
} from "./PostItem.style";
import Avatar from "../Avatar/Avatar";
import CircleIcon from "../CircleIcon/CircleIcon";
import Comment from "../Comment/Comment";
import ImageSlider from "../ImageSlider/ImageSlider";
import Chip from "../Chip/Chip";
import { CircleButtonItem, Post } from "../../../@types";
import { PostHeartIcon, PostHeartLineIcon, GithubIcon, ArrowRightIcon } from "../../../assets/icons";
import { useContext, useRef, useState } from "react";
import { ThemeContext } from "styled-components";
import { PAGE_URL } from "../../../constants/urls";
import { LIMIT } from "../../../constants/limits";
import { getTimeDiffFromCurrent } from "../../../utils/date";
import EmptyPostImage from "../../../assets/images/empty-post-image.png";
import ButtonDrawer from "../ButtonDrawer/ButtonDrawer";
import { getTextElementsWithBr } from "../../../utils/text";
import ShareLink from "../ShareLink/ShareLink";
import useSnackbar from "../../../hooks/common/useSnackbar";

export interface Props {
  post: Post;
  currentUserName: string;
  isEditable: boolean;
  isLoggedIn: boolean;
  handlePostLikeCountClick: () => void;
  onMoreCommentClick: () => void;
  onCommentInputClick: () => void;
  onPostEdit: () => void;
  onPostDelete: () => void;
  onPostLike: () => void;
}

const timeDiffTextTable = {
  sec: () => "방금 전",
  min: (time: number) => `${time}분 전`,
  hour: (time: number) => `${time}시간 전`,
  day: (time: number) => `${time}일 전`,
};

const PostItem = ({
  post,
  currentUserName,
  isLoggedIn,
  isEditable,
  handlePostLikeCountClick,
  onMoreCommentClick,
  onCommentInputClick,
  onPostEdit,
  onPostDelete,
  onPostLike,
}: Props) => {
  const [shouldHideContent, setShouldHideContent] = useState(true);
  const KakaoLinkButtonRef = useRef<HTMLButtonElement>(null);
  const { color } = useContext(ThemeContext);
  const { pushSnackbarMessage } = useSnackbar();

  const { min, hour, day } = getTimeDiffFromCurrent(post.createdAt);
  const currentTimeDiffText = day
    ? timeDiffTextTable.day(day)
    : hour
    ? timeDiffTextTable.hour(hour)
    : min
    ? timeDiffTextTable.min(min)
    : timeDiffTextTable.sec();

  const handleShareKakaoLink = () => {
    if (!KakaoLinkButtonRef.current) {
      return;
    }

    KakaoLinkButtonRef.current.click();
  };

  const handleShareLinkCopy = () => {
    navigator.clipboard.writeText(PAGE_URL.POST_SHARE(post.id));
    pushSnackbarMessage(`게시글 링크가 복사되었습니다`);
  };

  const circleButtons: CircleButtonItem[] = [
    { icon: "EditIcon", onClick: onPostEdit },
    { icon: "TrashIcon", onClick: onPostDelete },
  ];

  const shareButtons: CircleButtonItem[] = [
    {
      icon: "KakaoIcon",
      backgroundColor: "#ffe812",
      onClick: handleShareKakaoLink,
    },
    {
      icon: "CopyIcon",
      onClick: handleShareLinkCopy,
    },
  ];

  const commentList = post.comments.map((comment) => (
    <CommentWrapper key={comment.id}>
      <Comment
        content={comment.content}
        authorName={comment.authorName}
        link={currentUserName === comment.authorName ? PAGE_URL.MY_PROFILE : PAGE_URL.USER_PROFILE(comment.authorName)}
      />
    </CommentWrapper>
  ));

  const tagList = post.tags.map((tag: string) => (
    <TagItemLinkButton key={tag} to={PAGE_URL.SEARCH_POST_BY_TAG(tag)}>
      <Chip>{tag}</Chip>
    </TagItemLinkButton>
  ));

  const LikeButton = () => {
    return isLoggedIn ? (
      <IconLink onClick={onPostLike}>{post.liked ? <PostHeartIcon /> : <PostHeartLineIcon />}</IconLink>
    ) : (
      <></>
    );
  };

  const handleMoreContentShow = () => {
    setShouldHideContent(false);
  };

  const handleMoreContentHide = () => {
    setShouldHideContent(true);
  };

  const shouldShowContentHideToggleButton =
    post.content.length > LIMIT.POST_CONTENT_HIDE_LENGTH || tagList.length > LIMIT.POST_TAG_HIDE_LENGTH;

  const shouldHideDots = post.content.length > LIMIT.POST_CONTENT_HIDE_LENGTH && shouldHideContent;

  const postContent = shouldHideContent
    ? getTextElementsWithBr(post.content.substring(0, LIMIT.POST_CONTENT_HIDE_LENGTH))
    : getTextElementsWithBr(post.content);

  return (
    <Container>
      <ShareLink target={post} cssProp={ShareLinkCSS} username={post.authorName}>
        <button ref={KakaoLinkButtonRef}>test</button>
      </ShareLink>
      <PostHeader>
        <PostAuthorInfoLink to={PAGE_URL.USER_PROFILE(post.authorName)}>
          <Avatar diameter="1.9375rem" imageUrl={post.profileImageUrl} />
          <PostAuthorName>{post.authorName}</PostAuthorName>
        </PostAuthorInfoLink>
        <PostHeaderButtonsWrapper>
          <ButtonDrawer icon="ShareIcon" circleButtons={shareButtons} containerCssProp={ShareButtonDrawerCSS} />
          {isEditable && <ButtonDrawer circleButtons={circleButtons} />}
        </PostHeaderButtonsWrapper>
      </PostHeader>
      <ImageSlider
        imageUrls={post.imageUrls.length !== 0 ? post.imageUrls : [EmptyPostImage]}
        slideButtonKind="in-box"
      />
      <PostBody>
        <IconLinkButtonsWrapper>
          <LikeButton />
          <IconLink href={post.githubRepoUrl} target="_blank">
            <CircleIcon diameter="1.625rem" backgroundColor={color.tertiaryColor}>
              <GithubIcon />
            </CircleIcon>
          </IconLink>
        </IconLinkButtonsWrapper>
        <LikeCountText onClick={handlePostLikeCountClick}>좋아요 {post.likesCount}개</LikeCountText>
        <PostContent>
          <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(post.authorName)}>{post.authorName}</PostContentAuthorLink>
          {postContent}
          {shouldHideDots && <span>...</span>}
        </PostContent>
        <TagListWrapper>{shouldHideContent ? tagList.slice(0, LIMIT.POST_TAG_HIDE_LENGTH) : tagList}</TagListWrapper>

        {shouldShowContentHideToggleButton && (
          <>
            {shouldHideContent ? (
              <MoreContentLinkButton onClick={handleMoreContentShow}>더보기</MoreContentLinkButton>
            ) : (
              <MoreContentLinkButton onClick={handleMoreContentHide}>간략히</MoreContentLinkButton>
            )}
          </>
        )}

        <CommentsWrapper>
          {commentList.length > 10
            ? commentList.slice(0, 10).concat(
                <MoreCommentExistIndicator key="more-contents-exist" onClick={onMoreCommentClick}>
                  ...
                </MoreCommentExistIndicator>
              )
            : commentList}
        </CommentsWrapper>
      </PostBody>
      <PostCreatedDateText>{currentTimeDiffText}</PostCreatedDateText>
      <CommentPageMoveLink onClick={onCommentInputClick}>
        <CommentPageMoveLinkText>{isLoggedIn ? "댓글 작성" : "댓글 보기"}</CommentPageMoveLinkText>
        <ArrowRightIcon />
      </CommentPageMoveLink>
    </Container>
  );
};

export default PostItem;
