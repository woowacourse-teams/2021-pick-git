import styled, { css, CSSProp } from "styled-components";
import * as icons from "../../../assets/icons";

export type IconType = keyof typeof icons;

export const iconTypes = Object.keys(icons) as IconType[];

export type Props = {
  /** 사용 할 아이콘 타입 */
  icon: IconType;
  /** 클릭 이벤트 핸들러 */
  onClick?: () => void;
  cssProp?: CSSProp;
};

const SVGIconWrapper = styled.div<{ isClickable: boolean; cssProp?: CSSProp }>(
  ({ cssProp, isClickable }) => css`
    ${cssProp}
    display: inline-block;

    transition: opacity 0.5s;

    ${isClickable &&
    ` 
      cursor: pointer;
      :hover {
        opacity: 0.5;
      }
    `}
  `
);

const SVGIcon = ({ icon, onClick, cssProp }: Props) => {
  const SVGIcon = icons[icon];

  return (
    <SVGIconWrapper isClickable={!!onClick} onClick={onClick} cssProp={cssProp}>
      <SVGIcon style={{ margin: "0px" }} />
    </SVGIconWrapper>
  );
};

export default SVGIcon;
