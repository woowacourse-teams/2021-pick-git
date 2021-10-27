import { getMBFromBite } from "../utils/postUpload";
import { LIMIT } from "./limits";

export const SUCCESS_MESSAGE = {
  LOGIN: "로그인 되었습니다.",
  LOGOUT: "로그아웃 되었습니다.",
  SET_PROFILE: "프로필을 수정했습니다.",
  POST_ADDED: "게시글이 추가되었습니다",
  POST_MODIFIED: "게시글이 수정되었습니다",
  POST_DELETED: "게시글이 삭제되었습니다",
};

export const FAILURE_MESSAGE = {
  NO_CONTENT_MODIFIED: "변경사항이 없습니다.",
  PROFILE_DESCRIPTION_MAX_LENGTH_EXCEEDED: `한 줄 소개는 ${LIMIT.PROFILE_DESCRIPTION_LENGTH}자 이하로만 입력할 수 있습니다.`,
  POST_CONTENT_LENGTH_LIMIT_EXCEEDED: `게시글은 ${LIMIT.POST_CONTENT_MAX_LENGTH}자 이하로만 입력할 수 있습니다.`,
  POST_FILE_SIZE_EXCEEDED: `총 용량이 ${getMBFromBite(
    LIMIT.POST_FILE_MAX_SIZE
  )}MB 이상인 파일들은 업로드 할 수 없습니다.`,
  POST_FILE_COUNT_EXCEEDED: `${LIMIT.POST_FILE_MAX_COUNT}개 이상의 파일은 업로드 할 수 없습니다.`,
  POST_TAG_LENGTH_LIMIT_EXCEEDED: `태그는 ${LIMIT.POST_TAG_LENGTH}자 이하로만 입력할 수 있습니다.`,
  POST_DUPLICATED_TAG_EXIST: "해당 태그와 중복된 태그가 이미 입력되어 있습니다.",
  POST_TAG_SPECIAL_SYMBOL_EXIST: "태그에는 -, _ 를 제외한 특수문자가 포함될 수 없습니다.",
  POST_REPOSITORY_NOT_SELECTED: "리포지터리를 선택해주세요.",
  POST_REPOSITORY_NOT_LOADABLE: "리포지터리 목록을 불러올 수 없습니다.",
  POST_FILE_AND_CONTENT_EMPTY: "이미지와 글 모두 작성되지 않았습니다.",
  POST_FILE: "이미지를 추가해주세요.",
  COMMENT_CONTENT_MAX_LENGTH_EXCEEDED: `댓글은 ${LIMIT.COMMENT_LENGTH}자 이하로만 입력할 수 있습니다.`,
  COMMENT_SAVE_FAILED: "댓글을 저장하지 못했습니다",
  SAME_CATEGORY_NAME_EXIST: "같은 이름의 소분류가 존재합니다",
  SHOULD_HAVE_LEAST_ONE_CATEGORY: "하나 이상의 소분류는 존재해야 합니다",
  SHOULD_HAVE_LEAST_ONE_DESCRIPTION: "하나 이상의 설명은 존재해야 합니다",
};

export const WARNING_MESSAGE = {
  POST_FILE_NOT_UPLOADED: "아무런 파일도 업로드 되지 않았습니다. 이대로 진행할까요?",
  POST_CONTENT_EMPTY: "글 내용이 비었습니다. 이대로 진행할까요?",
  POST_DELETE: "정말로 게시물을 삭제하시겠습니까?",
  GITHUB_FOLLOWING: "깃허브 계정에서도 팔로우 하시겠습니까?",
  GITHUB_UNFOLLOWING: "깃허브 계정에서도 팔로우 취소 하시겠습니까?",
  COMMENT_DELETE: "정말로 댓글을 삭제하시겠습니까?",
  NO_ONE_LIKE_POST: "해당 포스트에 좋아요를 누른 사람이 없습니다.",
};

export const NOT_FOUND_MESSAGE = {
  POSTS: {
    DEFAULT: "게시물이 존재하지 않습니다.",
    NETWORK: "게시물 정보를 가져올 수 없습니다.",
    FOLLOWINGS: "게시물이 존재하지 않습니다. 다른 사람을 팔로우해보세요",
    PROJECT: "프로젝트가 존재하지 않습니다. 게시물을 추가해보세요.",
  },
};

export const REDIRECT_MESSAGE = {
  NO_REPOSITORY_EXIST: "공개된 깃허브 Repository가 존재하지 않습니다. 이전 페이지로 돌아갑니다.",
};

export const API_ERROR_MESSAGE = {
  A0001: "로그인이 만료되었습니다",
  A0002: "로그인 후 이용할 수 있습니다",
  F0001: "필요한 입력값이 입력되지 않았습니다",
  F0002: "댓글 글자수가 형식에 맞지 않습니다",
  F0003: "태그 글자수가 형식에 맞지 않습니다",
  U0001: "해당 유저는 존재하지 않습니다",
  U0002: "이미 팔로우하고 있는 유저입니다",
  U0003: "존재하지 않는 유저에 대한 팔로우를 수행할 수 없습니다",
  U0004: "본인이 본인을 팔로우할 수 없습니다",
  V0001: "깃허브 로그인에 실패하였습니다",
  P0001: "중복되는 태그가 존재합니다",
  P0002: "게시된 게시글이 없습니다",
  S0001: "서버 상의 에러가 발생하였습니다.",
};

export const CLIENT_ERROR_MESSAGE = {
  C0001: "로그인이 만료되었습니다",
  C0002: "파일을 읽을 수 없습니다. 다시 시도해주세요.",
};

export const UNKNOWN_ERROR_MESSAGE = "알 수 없는 에러가 발생하였습니다.";
