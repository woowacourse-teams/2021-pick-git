import { CSSProp } from "styled-components";
import { KakaoLinkDefault } from "react-kakao-link";
import { Portfolio, PortfolioData, Post } from "../../../@types";
import {
  KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE,
  MY_KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE,
  KAKAO_POST_SHARE_LINK_TEMPLATE,
} from "../../../constants/kakao";

export interface Props {
  target: Post | Portfolio | PortfolioData;
  children: React.ReactNode;
  cssProp?: CSSProp;
  username: string;
}

const isPost = (target: Props["target"]): target is Post => {
  return !!(target as Post).githubRepoUrl;
};

const isMyPortfolio = (target: Props["target"]): target is Portfolio => {
  return !!(target as Portfolio).intro;
};

const ShareLink = ({ target, username, children, cssProp }: Props) => {
  if (isPost(target)) {
    const template = KAKAO_POST_SHARE_LINK_TEMPLATE(target);
    return (
      <KakaoLinkDefault cssProp={cssProp} template={template} jsKey={process.env.KAKAO_API_KEY}>
        {children}
      </KakaoLinkDefault>
    );
  }

  if (isMyPortfolio(target)) {
    const template = MY_KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE(target, username);
    return (
      <KakaoLinkDefault cssProp={cssProp} template={template} jsKey={process.env.KAKAO_API_KEY}>
        {children}
      </KakaoLinkDefault>
    );
  }

  const template = KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE(target, username);

  return (
    <KakaoLinkDefault cssProp={cssProp} template={template} jsKey={process.env.KAKAO_API_KEY}>
      {children}
    </KakaoLinkDefault>
  );
};

export default ShareLink;
