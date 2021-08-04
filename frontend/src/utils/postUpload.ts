import { PostEditData, PostUploadData } from "../@types";
import { LIMIT } from "../constants/limits";
import { FAILURE_MESSAGE } from "../constants/messages";

export const getMBFromBite = (bite: number) => {
  return Math.floor(bite / (1000 * 1000));
};

export const isContentEmpty = (content: PostUploadData["content"]) => {
  return content.length === 0;
};

export const isValidContentLength = (content: PostUploadData["content"]) => {
  return content.length <= LIMIT.POST_CONTENT_MAX_LENGTH;
};

export const isFilesEmpty = (files: PostUploadData["files"]) => {
  return files.length === 0;
};

export const isValidFilesSize = (files: PostUploadData["files"]) => {
  return files.reduce((acc, file) => acc + file.size, 0) < LIMIT.POST_FILE_MAX_SIZE;
};

export const isValidFilesSizeCount = (files: PostUploadData["files"]) => {
  return files.length <= LIMIT.POST_FILE_MAX_COUNT;
};

export const isValidTagLength = (tag: string) => {
  return tag.length <= LIMIT.POST_TAG_LENGTH;
};

export const isValidTagFormat = (tag: string) => {
  const regExp = /[\{\}\[\]\/?.,;:|\)*~`!^\+<>@\#$%&\\\=\(\'\"]/gi;

  return !regExp.test(tag);
};

export const isValidTagsFormat = (tags: PostUploadData["tags"]) => {
  return tags.every(isValidTagFormat);
};

export const hasDuplicatedTag = (tags: PostUploadData["tags"]) => {
  if (tags.length === 0) {
    return false;
  }
  return new Set(tags).size !== tags.length;
};

export const isValidTagLengths = (tags: PostUploadData["tags"]) => {
  return tags.every(isValidTagLength);
};

export const isGithubRepositoryEmpty = (githubRepositoryName: PostUploadData["githubRepositoryName"]) => {
  return githubRepositoryName !== "";
};

export const isValidPostUploadData = ({ content, tags, files, githubRepositoryName }: PostUploadData) => {
  return (
    isValidContentLength(content) &&
    isValidFilesSize(files) &&
    isValidFilesSizeCount(files) &&
    isValidTagLengths(tags) &&
    isGithubRepositoryEmpty(githubRepositoryName) &&
    (!isFilesEmpty(files) || !isContentEmpty(content))
  );
};

export const isValidPostEditData = ({ postId, content, tags }: PostEditData) => {
  return isValidContentLength(content) && isValidTagLengths(tags) && !isContentEmpty(content) && postId !== 0;
};

export const getPostAddValidationMessage = ({ content, tags, files, githubRepositoryName }: PostUploadData) => {
  if (!isGithubRepositoryEmpty(githubRepositoryName)) {
    return FAILURE_MESSAGE.POST_REPOSITORY_NOT_SELECTED;
  }

  if (!isValidContentLength(content)) {
    return FAILURE_MESSAGE.POST_CONTENT_LENGTH_LIMIT_EXCEEDED;
  }

  if (!isValidFilesSize(files)) {
    return FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED;
  }

  if (!isValidFilesSizeCount(files)) {
    return FAILURE_MESSAGE.POST_FILE_COUNT_EXCEEDED;
  }

  if (isContentEmpty(content) && isFilesEmpty(files)) {
    return FAILURE_MESSAGE.POST_FILE_AND_CONTENT_EMPTY;
  }

  if (!isValidTagsFormat(tags)) {
    return FAILURE_MESSAGE.POST_TAG_SPECIAL_SYMBOL_EXIST;
  }

  if (hasDuplicatedTag(tags)) {
    return FAILURE_MESSAGE.POST_DUPLICATED_TAG_EXIST;
  }

  if (!isValidTagLengths(tags)) {
    return FAILURE_MESSAGE.POST_TAG_LENGTH_LIMIT_EXCEEDED;
  }

  return "";
};

export const getPostEditValidationMessage = ({ content, tags }: PostEditData) => {
  if (!isValidContentLength(content)) {
    return FAILURE_MESSAGE.POST_CONTENT_LENGTH_LIMIT_EXCEEDED;
  }

  if (!isValidTagsFormat(tags)) {
    return FAILURE_MESSAGE.POST_TAG_SPECIAL_SYMBOL_EXIST;
  }

  if (hasDuplicatedTag(tags)) {
    return FAILURE_MESSAGE.POST_DUPLICATED_TAG_EXIST;
  }

  if (!isValidTagLengths(tags)) {
    return FAILURE_MESSAGE.POST_TAG_LENGTH_LIMIT_EXCEEDED;
  }

  return "";
};
