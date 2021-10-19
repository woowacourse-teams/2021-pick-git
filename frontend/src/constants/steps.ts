import { Step } from "../@types";

export const POST_ADD_STEPS: Step[] = [
  { title: "Git 리포지터리", hash: "repository" },
  { title: "이미지 & 글 작성", hash: "content" },
  { title: "태그 입력", hash: "tags" },
];

export const POST_EDIT_STEPS: Step[] = [
  { title: "글 수정", hash: "content" },
  { title: "태그 수정", hash: "tags" },
];

export const COMMENT_SLIDE_STEPS: Step[] = [
  { title: "사진/동영상", hash: "#files" },
  { title: "작성글", hash: "#content" },
  { title: "태그목록", hash: "#tags" },
];
