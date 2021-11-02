import { PortfolioContact, PortfolioIcon } from "../@types";

export const PORTFOLIO_CONTACTS: PortfolioContact[] = [
  {
    id: null,
    category: "github",
    value: "https://github.com/someone",
  },
  {
    id: null,
    category: "email",
    value: "email@gmail.com",
  },
  {
    id: null,
    category: "phone",
    value: "000-0000-0000",
  },
  {
    id: null,
    category: "website",
    value: "",
  },
  {
    id: null,
    category: "blog",
    value: "",
  },
  {
    id: null,
    category: "CV",
    value: "",
  },
  {
    id: null,
    category: "company",
    value: "",
  },
  {
    id: null,
    category: "address",
    value: "",
  },
];

export const CONTACT_CATEGORY_TEXT = {
  [PORTFOLIO_CONTACTS[0].category]: "깃허브",
  [PORTFOLIO_CONTACTS[1].category]: "이메일",
  [PORTFOLIO_CONTACTS[2].category]: "전화번호",
  [PORTFOLIO_CONTACTS[3].category]: "웹사이트",
  [PORTFOLIO_CONTACTS[4].category]: "블로그",
  [PORTFOLIO_CONTACTS[5].category]: "이력서 링크",
  [PORTFOLIO_CONTACTS[6].category]: "현재 직장",
  [PORTFOLIO_CONTACTS[7].category]: "현재 주소",
};

export const CONTACT_ICON: {
  [key: string]: PortfolioIcon;
} = {
  [PORTFOLIO_CONTACTS[0].category]: "GithubLineIcon",
  [PORTFOLIO_CONTACTS[1].category]: "EmailIcon",
  [PORTFOLIO_CONTACTS[2].category]: "PhoneIcon",
  [PORTFOLIO_CONTACTS[3].category]: "WebsiteIcon",
  [PORTFOLIO_CONTACTS[4].category]: "BlogIcon",
  [PORTFOLIO_CONTACTS[5].category]: "LinkIcon",
  [PORTFOLIO_CONTACTS[6].category]: "CompanyLineIcon",
  [PORTFOLIO_CONTACTS[7].category]: "LocationLineIcon",
};

export const TEMP_ID_INDICATOR = "temp-";
