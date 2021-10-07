import React from "react";
import { render, fireEvent } from "@testing-library/react";
import { renderHook, act } from "@testing-library/react-hooks";
import { QueryClientProvider } from "react-query";

import { ProfileData } from "../../@types";
import { QUERY } from "../../constants/queries";
import UserContext from "../../contexts/UserContext";
import SnackBarContext from "../../contexts/SnackbarContext";
import useProfileModificationForm from "../service/useProfileModificationForm";
import {
  IMAGE_FILE,
  IMAGE_FILE_URL,
  IMAGE_NAME,
  MockedForm,
  NEW_DESCRIPTION,
  NEW_IMAGE_URL,
  PREV_DESCRIPTION,
  PREV_IMAGE_URL,
  profileModificationServer,
} from "../@mocks/mockProfileModification";
import {
  createQueryClient,
  DEFAULT_PROFILE_QUERY_DATA,
  File,
  mockFn,
  setLocalStorageEmpty,
  setLocalStorageInvalid,
  setLocalStorageValid,
  UNAUTHORIZED_TOKEN_ERROR,
  USERNAME,
} from "../@mocks/shared";
import { LIMIT } from "../../constants/limits";
import { API_ERROR_MESSAGE, CLIENT_ERROR_MESSAGE, FAILURE_MESSAGE } from "../../constants/messages";

const queryClient = createQueryClient();
const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: true, username: USERNAME }];

const wrapper = ({ children }: { children: React.ReactNode }) => {
  queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
    ...DEFAULT_PROFILE_QUERY_DATA,
    imageUrl: PREV_IMAGE_URL,
    description: PREV_DESCRIPTION,
  });

  const userContextValue = { isLoggedIn: true, currentUsername: USERNAME, login: () => {}, logout: mockFn.logout };
  const snackbarContextValue = { pushSnackbarMessage: mockFn.pushSnackbarMessage };

  return (
    <QueryClientProvider client={queryClient}>
      <UserContext.Provider value={userContextValue}>
        <SnackBarContext.Provider value={snackbarContextValue}>{children}</SnackBarContext.Provider>
      </UserContext.Provider>
    </QueryClientProvider>
  );
};

const initialValue = {
  imageUrl: PREV_IMAGE_URL,
  description: PREV_DESCRIPTION,
};

const setupHook = () =>
  renderHook(() => useProfileModificationForm(USERNAME, initialValue, mockFn.messageViewer, mockFn.submitEffect), {
    wrapper,
  });

const setupFormWithHook = () => {
  const { result, waitFor } = setupHook();
  const form = render(
    <MockedForm
      onImageChange={(event) => result.current.handlers.handleImageChange(event)}
      onDescriptionChange={(event) => result.current.handlers.handleDescriptionChange(event)}
      onSubmit={(event) => result.current.handlers.handleModificationSubmit(event)}
    />
  );

  return {
    imageInput: form.getByTestId("image-input"),
    descriptionTextarea: form.getByTestId("description-input"),
    form: form.getByTestId("form"),
    result,
    waitFor,
  };
};

const actions = {
  changeImage: async (image: File, setupFormResult: ReturnType<typeof setupFormWithHook>) => {
    const { result, waitFor, imageInput } = setupFormResult;

    jest.clearAllMocks();
    fireEvent.change(imageInput, { target: { files: [image] } });
    await waitFor(() => result.current.values.image !== null || mockFn.messageViewer.mock.calls.length === 1);
  },

  changeDescription: async (description: string, setupFormResult: ReturnType<typeof setupFormWithHook>) => {
    const { result, waitFor, descriptionTextarea } = setupFormResult;

    jest.clearAllMocks();
    fireEvent.change(descriptionTextarea, { target: { value: description } });
    await waitFor(
      () => result.current.values.description !== PREV_DESCRIPTION || mockFn.messageViewer.mock.calls.length === 1
    );
  },

  submitImage: async (setupFormResult: ReturnType<typeof setupFormWithHook>) => {
    const { form, waitFor } = setupFormResult;

    jest.clearAllMocks();
    fireEvent.submit(form);
    await waitFor(
      () =>
        queryClient.getQueryData<ProfileData>(currentProfileQueryKey)?.imageUrl !== PREV_IMAGE_URL ||
        mockFn.messageViewer.mock.calls.length === 1 ||
        mockFn.pushSnackbarMessage.mock.calls.length === 1
    );
  },

  submitDescription: async (setupFormResult: ReturnType<typeof setupFormWithHook>) => {
    const { form, waitFor } = setupFormResult;

    jest.clearAllMocks();
    fireEvent.submit(form);
    await waitFor(
      () =>
        queryClient.getQueryData<ProfileData>(currentProfileQueryKey)?.description !== PREV_DESCRIPTION ||
        mockFn.messageViewer.mock.calls.length === 1 ||
        mockFn.pushSnackbarMessage.mock.calls.length === 1
    );
  },
};

beforeAll(() => {
  URL.createObjectURL = jest.fn(() => IMAGE_FILE_URL);
  setLocalStorageValid();
  profileModificationServer.listen();
});

afterAll(() => {
  profileModificationServer.close();
});

describe("Success Case", () => {
  test("success1: should init values", () => {
    const { result } = setupHook();

    expect(result.current.values.image).toBe(null);
    expect(result.current.values.imageUrl).toBe(PREV_IMAGE_URL);
    expect(result.current.values.description).toBe(PREV_DESCRIPTION);
  });

  test("success2: should change image file state", async () => {
    const setupFormResult = setupFormWithHook();
    const { result } = setupFormResult;

    await act(() => actions.changeImage(IMAGE_FILE, setupFormResult));

    expect(result.current.values.image).toBe(IMAGE_FILE);
    expect(result.current.values.imageUrl).toBe(IMAGE_FILE_URL);
  });

  test("success3: should change description state", async () => {
    const setupFormResult = setupFormWithHook();
    const { result } = setupFormResult;

    await act(() => actions.changeDescription(NEW_DESCRIPTION, setupFormResult));

    expect(result.current.values.description).toBe(NEW_DESCRIPTION);
  });

  test("success4: should modify profile image", async () => {
    const setupFormResult = setupFormWithHook();

    await act(() => actions.changeImage(IMAGE_FILE, setupFormResult));
    await act(() => actions.submitImage(setupFormResult));

    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(currentProfileQueryData?.imageUrl).toBe(NEW_IMAGE_URL);
  });

  test("success5: should modify description", async () => {
    const setupFormResult = setupFormWithHook();

    await act(() => actions.changeDescription(NEW_DESCRIPTION, setupFormResult));
    await act(() => actions.submitDescription(setupFormResult));

    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(currentProfileQueryData?.description).toBe(NEW_DESCRIPTION);
  });
});

describe("Failure Case", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("failure1: should validate image file size", async () => {
    const setupFormResult = setupFormWithHook();
    const { result } = setupFormResult;
    const bigImageFile = new File(Array(LIMIT.POST_FILE_MAX_SIZE + 100).fill("t"), IMAGE_NAME);

    await act(() => actions.changeImage(bigImageFile, setupFormResult));

    expect(result.current.values.image).toBe(null);
    expect(result.current.values.imageUrl).toBe(PREV_IMAGE_URL);
    expect(mockFn.messageViewer.mock.calls[0][0]).toBe(FAILURE_MESSAGE.POST_FILE_SIZE_EXCEEDED);
  });

  test("failure2: should validate description length", async () => {
    const setupFormResult = setupFormWithHook();
    const { result } = setupFormResult;
    const longDescription = "a".repeat(LIMIT.PROFILE_DESCRIPTION_LENGTH + 200);

    await act(() => actions.changeDescription(longDescription, setupFormResult));

    expect(result.current.values.description).toBe(PREV_DESCRIPTION);
    expect(mockFn.messageViewer.mock.calls[0][0]).toBe(FAILURE_MESSAGE.PROFILE_DESCRIPTION_MAX_LENGTH_EXCEEDED);
  });

  test("failure3: should block submit when profile modification form doesn't change", async () => {
    const setupFormResult = setupFormWithHook();

    await act(() => actions.submitImage(setupFormResult));

    expect(mockFn.messageViewer.mock.calls[0][0]).toBe(FAILURE_MESSAGE.NO_CONTENT_MODIFIED);
  });

  test("failure4: should handle empty accessToken error while one change image", async () => {
    setLocalStorageEmpty();

    const setupFormResult = setupFormWithHook();

    await act(() => actions.changeImage(IMAGE_FILE, setupFormResult));
    await act(() => actions.submitImage(setupFormResult));

    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(mockFn.logout.mock.calls.length).toBe(1);
    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(CLIENT_ERROR_MESSAGE.C0001);
    expect(currentProfileQueryData?.imageUrl).toBe(PREV_IMAGE_URL);

    setLocalStorageValid();
  });

  test("failure5: should handle empty accessToken error while one change description", async () => {
    setLocalStorageEmpty();

    const setupFormResult = setupFormWithHook();

    await act(() => actions.changeDescription(NEW_DESCRIPTION, setupFormResult));
    await act(() => actions.submitDescription(setupFormResult));

    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(mockFn.logout.mock.calls.length).toBe(1);
    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(CLIENT_ERROR_MESSAGE.C0001);
    expect(currentProfileQueryData?.description).toBe(PREV_DESCRIPTION);

    setLocalStorageValid();
  });

  test("failure6: should handle http error while one change image: 401", async () => {
    setLocalStorageInvalid();

    const setupFormResult = setupFormWithHook();

    await act(() => actions.changeImage(IMAGE_FILE, setupFormResult));
    await act(() => actions.submitImage(setupFormResult));

    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockFn.logout.mock.calls.length).toBe(1);
    expect(currentProfileQueryData?.imageUrl).toBe(PREV_IMAGE_URL);

    setLocalStorageValid();
  });

  test("failure7: should handle http error while one change description: 401", async () => {
    setLocalStorageInvalid();

    const setupFormResult = setupFormWithHook();

    await act(() => actions.changeDescription(NEW_DESCRIPTION, setupFormResult));
    await act(() => actions.submitDescription(setupFormResult));

    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    expect(mockFn.pushSnackbarMessage.mock.calls[0][0]).toBe(API_ERROR_MESSAGE[UNAUTHORIZED_TOKEN_ERROR]);
    expect(mockFn.logout.mock.calls.length).toBe(1);
    expect(currentProfileQueryData?.description).toBe(PREV_DESCRIPTION);

    setLocalStorageValid();
  });
});
