import { RefObject, useRef } from "react";
import { CSSProp } from "styled-components";
import defaultImage from "../../../assets/images/default-image.png";
import { Container, Image } from "./ImageUploader.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  defaultImageSrc?: string;
  imageUploaderRef?: RefObject<HTMLImageElement>;
  onFileListSave: (fileList: FileList) => void;
  cssProp?: CSSProp;
}

const ImageUploader = ({
  defaultImageSrc = defaultImage,
  imageUploaderRef,
  onFileListSave,
  cssProp,
  ...props
}: Props) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleImageUpload = () => {
    if (!fileInputRef.current) {
      alert("hey!!!");
    }

    fileInputRef.current?.click();
  };

  const handleFileSave: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    if (!event.currentTarget.files) {
      return;
    }

    onFileListSave(event.currentTarget.files);
  };

  return (
    <Container {...props} cssProp={cssProp}>
      <Image src={defaultImageSrc} onClick={handleImageUpload} ref={imageUploaderRef} />
      <input
        type="file"
        multiple
        accept=".jpg, .png, .jpeg, tiff, .gif"
        style={{ display: "none" }}
        ref={fileInputRef}
        onChange={handleFileSave}
      />
    </Container>
  );
};

export default ImageUploader;
