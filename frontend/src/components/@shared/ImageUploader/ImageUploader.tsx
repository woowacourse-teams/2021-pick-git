import { useRef } from "react";
import defaultImage from "../../../assets/images/default-image.png";
import { Container, Image } from "./ImageUploader.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  defaultImageSrc?: string;
  onFileListSave: (fileList: FileList) => void;
}

const ImageUploader = ({ defaultImageSrc = defaultImage, onFileListSave, ...props }: Props) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleImageUpload = () => {
    fileInputRef.current?.click();
  };

  const handleFileSave: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    if (!event.currentTarget.files) {
      return;
    }

    onFileListSave(event.currentTarget.files);
  };

  return (
    <Container {...props}>
      <Image src={defaultImageSrc} onClick={handleImageUpload} />
      <input
        type="file"
        multiple
        accept=".jpg, .png, .jpeg, tiff"
        style={{ display: "none" }}
        ref={fileInputRef}
        onChange={handleFileSave}
      />
    </Container>
  );
};

export default ImageUploader;
