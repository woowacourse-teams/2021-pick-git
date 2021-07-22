import styled from 'styled-components';

interface Props {
  zIndex?: number;
}

export const BackDrop = styled.div<Props>`
  position: fixed;
  z-index: ${({ zIndex }) => zIndex};
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
`;
