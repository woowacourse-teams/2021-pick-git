import { getMBFromBite } from "../utils/postUpload";
import { LIMIT } from "./limits";

export const FAILURE_MESSAGE = {
  COMMENT_CONTENT_MAX_LENGTH_EXCEEDED: `댓글은 ${LIMIT.COMMENT_LENGTH}자 이하로만 입력할 수 있습니다.`,
  POST_CONTENT_LENGTH_LIMIT_EXCEEDED: `게시글은 ${LIMIT.POST_CONTENT_MAX_LENGTH}자 이하로만 입력할 수 있습니다.`,
  POST_FILE_SIZE_EXCEEDED: `용량이 ${getMBFromBite(LIMIT.POST_FILE_MAX_SIZE)}MB 이상인 파일은 업로드 할 수 없습니다.`,
  POST_FILE_COUNT_EXCEEDED: `${LIMIT.POST_FILE_MAX_COUNT}개 이상의 파일은 업로드 할 수 없습니다.`,
  POST_TAG_LENGTH_LIMIT_EXCEEDED: `태그는 ${LIMIT.POST_TAG_LENGTH}자 이하로만 입력할 수 있습니다.`,
  POST_DUPLICATED_TAG_EXIST: `해당 태그와 중복된 태그가 이미 입력되어 있습니다.`,
  POST_TAG_SPECIAL_SYMBOL_EXIST: `태그에는 -, _ 를 제외한 특수문자가 포함될 수 없습니다.`,
  POST_REPOSITORY_NOT_SELECTED: "리포지터리를 선택해주세요",
  POST_FILE_AND_CONTENT_EMPTY: "이미지와 글 모두 작성되지 않았습니다.",
};

export const WARNING_MESSAGE = {
  POST_FILE_NOT_UPLOADED: "아무런 파일도 업로드 되지 않았습니다. 이대로 진행할까요?",
  POST_CONTENT_EMPTY: "글 내용이 비었습니다. 이대로 진행할까요?",
};

export const REDIRECT_MESSAGE = {
  NO_REPOSITORY_EXIST: "공개된 깃허브 Repository가 존재하지 않습니다. 이전 페이지로 돌아갑니다.",
};
