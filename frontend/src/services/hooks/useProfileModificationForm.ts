import { useContext, useEffect, useState } from "react";
import { useQueryClient } from "react-query";
import axios from "axios";

import { ProfileData } from "../../@types";
import { FAILURE_MESSAGE, SUCCESS_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import { QUERY } from "../../constants/queries";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { useProfileMutation } from "../../services/queries";
import { getAPIErrorMessage, getClientErrorMessage, handleClientError, handleHTTPError } from "../../utils/error";
import { isValidFileSize, isValidProfileDescription } from "../../utils/profileModification";
import { isClientErrorCode, isHttpErrorStatus } from "../../utils/typeGuard";

const useProfileModificationForm = (
  username: string,
  initialValue: { imageUrl?: string; description?: string },
  messageViewer?: (message: string) => void,
  submitEffect?: () => void
) => {
  const [image, setImage] = useState<File | null>(null);
  const [imageUrl, setImageUrl] = useState(initialValue.imageUrl ?? "");
  const [description, setDescription] = useState(initialValue.description ?? "");

  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const queryClient = useQueryClient();
  const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: true, username }];
  const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);
  const { mutate, data, error, isLoading, isSuccess } = useProfileMutation();

  const handleImageChange: React.ChangeEventHandler<HTMLInputElement> = ({ currentTarget: { files } }) => {
    if (!files) return;

    if (!isValidFileSize(files[0])) {
      messageViewer?.(FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED);

      return;
    }

    setImage(files[0]);
    setImageUrl(URL.createObjectURL(files[0]));
  };

  const handleDescriptionChange: React.ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => {
    setDescription(value);
  };

  const submitValidation = () => {
    if (!image && initialValue.description === description) {
      messageViewer?.(FAILURE_MESSAGE.NO_CONTENT_MODIFIED);

      return false;
    }

    if (!isValidProfileDescription(description)) {
      messageViewer?.(FAILURE_MESSAGE.PROFILE_DESCRIPTION_MAX_LENGTH_EXCEEDED);

      return false;
    }

    return true;
  };

  const handleModificationSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();

    if (!submitValidation()) {
      return;
    }

    mutate({ image, description });
  };

  const handleMutationResultFetch = () => {
    if (!data || !currentProfileQueryData) {
      return;
    }

    queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
      ...currentProfileQueryData,
      imageUrl: data.imageUrl,
      description: data.description,
    });

    pushSnackbarMessage(SUCCESS_MESSAGE.SET_PROFILE);
  };

  const handleError = () => {
    if (!error) {
      return;
    }

    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => logout(),
          notFound: () => pushSnackbarMessage("아직 준비되지 않은 서비스입니다."),
          methodNotAllowed: () => pushSnackbarMessage("아직 준비되지 않은 서비스입니다."),
        });
      }

      pushSnackbarMessage(data ? getAPIErrorMessage(data.errorCode) : UNKNOWN_ERROR_MESSAGE);
    } else {
      const { message } = error;

      if (isClientErrorCode(message)) {
        handleClientError(message, {
          noAccessToken: () => {
            logout();
          },
        });
        pushSnackbarMessage(getClientErrorMessage(message));
      } else {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      }
    }
  };

  useEffect(() => {
    if (isSuccess) {
      submitEffect?.();
    }
  }, [isSuccess]);

  useEffect(() => {
    handleMutationResultFetch();
  }, [data]);

  useEffect(() => {
    handleError();
  }, [error]);

  return {
    values: { image, imageUrl, description },
    handlers: { handleImageChange, handleDescriptionChange, handleModificationSubmit },
    isLoading,
  };
};

export default useProfileModificationForm;