import { CSSProp } from "styled-components";

import {
  KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE,
  MY_KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE,
  KAKAO_POST_SHARE_LINK_TEMPLATE,
} from "../../../constants/kakao";

import { sendKakaoShareLink } from "../../../utils/kakao";

import { Container } from "./ShareLink.style";
import { Portfolio, PortfolioData, Post } from "../../../@types";

export interface Props {
  target: Post | Portfolio | PortfolioData;
  children?: React.ReactNode;
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
      <Container cssProp={cssProp} onClick={() => sendKakaoShareLink(template)}>
        {children}
      </Container>
    );
  }

  if (isMyPortfolio(target)) {
    const template = MY_KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE(target, username);

    return (
      <Container cssProp={cssProp} onClick={() => sendKakaoShareLink(template)}>
        {children}
      </Container>
    );
  }

  const template = KAKAO_PORTFOLIO_SHARE_LINK_TEMPLATE(target, username);

  return (
    <Container cssProp={cssProp} onClick={() => sendKakaoShareLink(template)}>
      {children}
    </Container>
  );
};

export default ShareLink;
