import { LIMIT } from "../constants/limits";

export const isValidFileSize = (file: File) => {
  return file.size <= LIMIT.POST_FILE_MAX_SIZE;
};

export const isValidProfileDescription = (description: string) => {
  return description.length <= LIMIT.PROFILE_DESCRIPTION_LENGTH;
};
