import { Post, Portfolio, PortfolioData } from "../@types";
import { PAGE_URL } from "./urls";

export const KAKAO_POST_SHARE_LINK_TEMPLATE = (post: Post) => ({
  objectType: "feed",
  content: {
    title: `${post.authorName}의 게시글`,
    description: post.content,
    imageUrl: post.imageUrls[0],
    link: {
      mobileWebUrl: PAGE_URL.POST_SHARE(post.id),
      webUrl: PAGE_URL.POST_SHARE(post.id),
    },
  },
  social: {
    likeCount: post.likesCount,
    commentCount: post.comments.length,
  },
  buttons: [
    {
      title: "게시글 보러가기",
      link: {
        mobileWebUrl: PAGE_URL.POST_SHARE(post.id),
        webUrl: PAGE_URL.POST_SHARE(post.id),
      },
    },
  ],
});

export const MY_KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE = (portfolio: Portfolio, username: string) => ({
  objectType: "feed",
  content: {
    title: `${portfolio.intro.name}의 포트폴리오`,
    description: portfolio.intro.description,
    imageUrl: portfolio.intro.profileImageUrl,
    link: {
      mobileWebUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
      webUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
    },
  },
  buttons: [
    {
      title: "포트폴리오 보러가기",
      link: {
        mobileWebUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
        webUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
      },
    },
  ],
});

export const KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE = (portfolio: PortfolioData, username: string) => ({
  objectType: "feed",
  content: {
    title: `${portfolio.name}의 포트폴리오`,
    description: portfolio.introduction,
    imageUrl: portfolio.profileImageUrl,
    link: {
      mobileWebUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
      webUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
    },
  },
  buttons: [
    {
      title: "포트폴리오 보러가기",
      link: {
        mobileWebUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
        webUrl: PAGE_URL.USER_PORTFOLIO_SHARE(username),
      },
    },
  ],
});
