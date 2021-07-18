import { Link } from "react-router-dom";
import styled from "styled-components";

export const Container = styled.div``;

export const PostHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
`;

export const PostAuthorInfoLink = styled(Link)`
  display: flex;
  align-items: center;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.7;
  }
`;

export const PostAuthorName = styled.span`
  margin-left: 1rem;
  font-weight: bold;
  font-size: 0.875rem;
`;

export const PostBody = styled.div`
  display: flex;
  flex-direction: column;
  padding: 0.75rem;
`;

export const IconLink = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.7;
  }
`;

export const IconLinkButton = styled(Link)`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const IconLinkButtonsWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.4375rem;
`;

export const LikeCountText = styled.span`
  font-weight: bold;
  margin-bottom: 0.5rem;
  font-size: 0.75rem;
`;

export const MoreContentLinkButton = styled.a`
  font-size: 0.75rem;
  font-weight: bold;
  margin-left: 0.625rem;
  margin-right: 0.3125rem;
  float: right;
`;

export const PostContentAuthorLink = styled(Link)`
  margin-right: 0.4375rem;
  font-size: 0.75rem;
  font-weight: bold;
`;

export const PostContent = styled.span`
  font-size: 0.625rem;
  line-height: 1.5rem;
  margin-bottom: 1rem;
`;

export const TagListWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 1rem;
`;

export const TagItemLinkButton = styled(Link)`
  margin-right: 0.625rem;
  margin-bottom: 0.5625rem;
`;

export const CommentWrapper = styled.div`
  margin-bottom: 0.5rem;
`;

export const MyComment = styled.div`
  display: flex;
  align-items: center;
  padding: 0 0.75rem;
`;

export const CommentInputWrapper = styled.div`
  width: 100%;
  padding: 0 1rem;
`;

export const PostCreatedDateText = styled.span`
  padding: 0.75rem;
  font-size: 0.6875rem;
  font-weight: bold;
`;
