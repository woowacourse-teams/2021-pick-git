export const getTextElementsWithWithBr = (text: string) => {
  return text.split("\n").map((textLine) => (
    <span>
      {textLine}
      <br />
    </span>
  ));
};
