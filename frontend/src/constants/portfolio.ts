import { PortfolioIcon } from "../@types";

export const CONTACTS = [
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
  [CONTACTS[0].category]: "깃허브",
  [CONTACTS[1].category]: "이메일",
  [CONTACTS[2].category]: "전화번호",
  [CONTACTS[3].category]: "웹사이트",
  [CONTACTS[4].category]: "블로그",
  [CONTACTS[5].category]: "이력서 링크",
  [CONTACTS[6].category]: "현재 직장",
  [CONTACTS[7].category]: "현재 주소",
};

export const CONTACT_ICON: {
  [key: string]: PortfolioIcon;
} = {
  [CONTACTS[0].category]: "GithubLineIcon",
  [CONTACTS[1].category]: "EmailIcon",
  [CONTACTS[2].category]: "PhoneIcon",
  [CONTACTS[3].category]: "WebsiteIcon",
  [CONTACTS[4].category]: "BlogIcon",
  [CONTACTS[5].category]: "LinkIcon",
  [CONTACTS[6].category]: "CompanyLineIcon",
  [CONTACTS[7].category]: "LocationLineIcon",
};

export const TEMP_ID_INDICATOR = "temp-";