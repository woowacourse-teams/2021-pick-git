import styled, { css, CSSProp } from "styled-components";
import * as icons from "../../../assets/icons";

export type IconType = keyof typeof icons;

export const iconTypes = Object.keys(icons) as IconType[];

export type Props = {
  /** 사용 할 아이콘 타입 */
  icon: IconType;
  cssProp?: CSSProp;
  /** 클릭 이벤트 핸들러 */
  onClick?: () => void;
};

const SVGIconWrapper = styled.div<{ isClickable: boolean; cssProp?: CSSProp }>(
  ({ cssProp, isClickable }) => css`
    display: inline-block;

    transition: opacity 0.5s;

    ${isClickable &&
    ` 
    cursor: pointer;
    :hover {
      opacity: 0.5;
    }
    `}
    ${cssProp}
  `
);

const SVGIcon = ({ icon, cssProp, onClick }: Props) => {
  const SVGIcon = icons[icon];

  return (
    <SVGIconWrapper isClickable={!!onClick} onClick={onClick} cssProp={cssProp}>
      <SVGIcon style={{ margin: "0px" }} />
    </SVGIconWrapper>
  );
};

export default SVGIcon;
