import React from "react";
import { rest } from "msw";
import { setupServer } from "msw/node";

import { File, UNAUTHORIZED_TOKEN_ERROR, VALID_ACCESS_TOKEN } from "./shared";

export const PREV_IMAGE_URL = "prev_image_url";
export const NEW_IMAGE_URL = "new_image_url";
export const IMAGE_NAME = "image_name.png";
export const IMAGE_FILE = new File(["asdf"], IMAGE_NAME, { type: "image/png" });
export const IMAGE_FILE_URL = "image_file_url";

export const PREV_DESCRIPTION = "prev_description";
export const NEW_DESCRIPTION = "new_description";

interface FormProps {
  onImageChange: React.ChangeEventHandler<HTMLInputElement>;
  onDescriptionChange: React.ChangeEventHandler<HTMLTextAreaElement>;
  onSubmit: React.FormEventHandler<HTMLFormElement>;
}

export const MockedForm = (props: FormProps) => (
  <form data-testid="form" onSubmit={props.onSubmit}>
    <input type="file" data-testid="image-input" onChange={props.onImageChange} />
    <textarea data-testid="description-input" onChange={props.onDescriptionChange} />
  </form>
);

const URL = {
  PROFILE_IMAGE: "http://localhost:3000/api/profiles/me/image",
  PROFILE_DESCRIPTION: "http://localhost:3000/api/profiles/me/description",
};

export const profileModificationServer = setupServer(
  rest.put(URL.PROFILE_IMAGE, (req, res, ctx) => {
    return req.headers.has("Authorization") && req.headers.get("Authorization") === `Bearer ${VALID_ACCESS_TOKEN}`
      ? res(ctx.json({ imageUrl: NEW_IMAGE_URL }))
      : res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
  }),
  rest.put(URL.PROFILE_DESCRIPTION, (req, res, ctx) => {
    return req.headers.has("Authorization") && req.headers.get("Authorization") === `Bearer ${VALID_ACCESS_TOKEN}`
      ? res(ctx.json({ description: NEW_DESCRIPTION }))
      : res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
  })
);
