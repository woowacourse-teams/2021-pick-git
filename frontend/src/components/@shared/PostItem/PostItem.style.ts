import { Link } from "react-router-dom";
import styled, { css } from "styled-components";
import { fadeIn } from "../../@styled/keyframes";
import { setDesktopMediaQuery, setMobileMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div`
  color: ${({ theme }) => theme.color.textColor};
  animation: ${fadeIn} 1s forwards;
`;

export const PostHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
`;

export const PostHeaderButtonsWrapper = styled.div(
  () => css`
    display: flex;
    align-items: center;
  `
);

export const ShareLinkCSS = css`
  display: none;
`;

export const ShareButtonDrawerCSS = css`
  margin-right: 1rem;
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

export const LikeCountText = styled.a`
  font-weight: bold;
  margin-bottom: 0.5rem;
  font-size: 0.75rem;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const MoreContentLinkButton = styled.a`
  font-size: 0.75rem;
  font-weight: bold;
  margin-left: 0.625rem;
  margin-right: 0.3125rem;
  margin-bottom: 2rem;
  float: right;
  text-align: right;
`;

export const PostContentAuthorLink = styled(Link)`
  margin-right: 0.4375rem;

  font-size: 0.8rem;
  ${setMobileMediaQuery`
    font-size: 0.7rem;
  `}
  ${setDesktopMediaQuery`
    font-size: 0.9rem;
  `}

  font-weight: bold;
`;

export const PostContent = styled.span`
  font-size: 0.7rem;
  ${setMobileMediaQuery`
    font-size: 0.625rem;
  `}
  ${setDesktopMediaQuery`
    font-size: 0.8rem;
  `}

  line-height: 1.5rem;
  margin-bottom: 1rem;
`;

export const TagListWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 0.75rem;
`;

export const TagItemLinkButton = styled(Link)`
  margin-right: 0.625rem;
  margin-bottom: 0.5625rem;

  :hover {
    span {
      transition: opacity 0.5s;
      opacity: 0.7;
    }
  }

  :active {
    span {
      opacity: 0.7;
      filter: brightness(1.1);
    }
  }
`;

export const CommentsWrapper = styled.div``;

export const CommentWrapper = styled.div`
  margin-bottom: 0.5rem;
`;

export const CommentPageMoveLink = styled.a`
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 1rem;
  font-size: 0.625rem;
  transition: opacity 0.5s;
  padding-bottom: 1rem;

  :hover {
    opacity: 0.5;
  }
`;

export const CommentPageMoveLinkText = styled.span`
  font-size: 0.75rem;
  font-weight: bold;
  margin-right: 0.5rem;
  margin-bottom: 0.1rem;
`;

export const MoreCommentExistIndicator = styled.div`
  text-align: center;
  cursor: pointer;
`;

export const CommentInputWrapper = styled.div`
  width: 100%;
  padding: 0 1rem;
`;

export const PostCreatedDateText = styled.span`
  padding: 0.75rem;
  font-size: 0.75rem;
  font-weight: bold;
`;
