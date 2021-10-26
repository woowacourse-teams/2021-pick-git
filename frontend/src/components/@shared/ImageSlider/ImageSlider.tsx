import { useContext, useEffect, useRef, useState } from "react";
import { CSSProp, ThemeContext } from "styled-components";

import { GoBackIcon, GoForwardIcon } from "../../../assets/icons/index";
import useThrottle from "../../../hooks/common/useThrottle";
import { Container, Image, ImageListItem, ImageListSlider, Indicator, SlideButton } from "./ImageSlider.style";

export interface Props extends React.CSSProperties {
  imageUrls: string[];
  slideButtonKind: "in-box" | "stick-out";
  cssProp?: CSSProp;
}

const SLIDE_THROTTLE_DELAY = 800;

const ImageSlider = ({ imageUrls, cssProp }: Props) => {
  const [imageIndex, setImageIndex] = useState(0);
  const [isFirstImage, setIsFirstImage] = useState(true);
  const [isLastImage, setIsLastImage] = useState(false);

  const theme = useContext(ThemeContext);
  const imageCount = useRef(imageUrls.length);
  const imageListSliderRef = useRef<HTMLUListElement>(null);

  const onImageListSliderScroll: React.WheelEventHandler<HTMLUListElement> = useThrottle((event) => {
    if (event.deltaX > 0 && !isLastImage) {
      setImageIndex((prevIndex) => prevIndex + 1);
    } else if (event.deltaX < 0 && !isFirstImage) {
      setImageIndex((prevIndex) => prevIndex - 1);
    }
  }, SLIDE_THROTTLE_DELAY);

  const onBackButtonClick: React.MouseEventHandler<HTMLButtonElement> = () => {
    if (isFirstImage) return;

    setImageIndex((prevIndex) => prevIndex - 1);
  };

  const onForwardButtonClick: React.MouseEventHandler<HTMLButtonElement> = () => {
    if (isLastImage) return;

    setImageIndex((prevIndex) => prevIndex + 1);
  };

  useEffect(() => {
    setIsFirstImage(imageIndex === 0);
    setIsLastImage(imageIndex === imageUrls.length - 1);

    if (imageListSliderRef.current) {
      imageListSliderRef.current.style.transform = `translateX(-${(100 * imageIndex) / imageUrls.length}%`;
    }
  }, [imageIndex, imageUrls.length]);

  return (
    <Container cssProp={cssProp}>
      <ImageListSlider ref={imageListSliderRef} width={`${imageUrls.length * 100}%`} onWheel={onImageListSliderScroll}>
        {imageUrls.map((imageUrl, index) => (
          <ImageListItem key={imageUrl}>
            <Image src={imageUrl} alt={`${index + 1}번째 사진`} />
          </ImageListItem>
        ))}
      </ImageListSlider>
      {!isFirstImage && (
        <SlideButton type="button" onClick={onBackButtonClick} direction="left">
          <GoBackIcon color={theme.color.white} />
        </SlideButton>
      )}
      {!isLastImage && (
        <SlideButton type="button" onClick={onForwardButtonClick} direction="right">
          <GoForwardIcon color={theme.color.white} />
        </SlideButton>
      )}
      {imageCount.current > 1 && (
        <Indicator>
          {imageIndex + 1} / {imageCount.current}
        </Indicator>
      )}
    </Container>
  );
};

export default ImageSlider;
