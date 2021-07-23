import * as Styled from './BackDrop.styles';

export interface Props {
  zIndex?: number;
  onBackDropClick?: () => void;
}

const BackDrop = ({ zIndex = -1, onBackDropClick }: Props) => {
  return <Styled.BackDrop onClick={onBackDropClick} zIndex={zIndex}></Styled.BackDrop>;
};

export default BackDrop;
