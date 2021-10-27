export const getTextElementsWithWithBr = (text: string) => {
  return text.split("\n").map((textLine, index) => (
    <span key={index}>
      {textLine}
      <br />
    </span>
  ));
};
