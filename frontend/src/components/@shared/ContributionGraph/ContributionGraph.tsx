import { useContext } from "react";
import { ThemeContext } from "styled-components";
import { Container, ContributionItem } from "./ContributionGraph.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  contributionItems: Array<"empty" | "normal" | "dense">;
  columnCount?: number;
  rowCount?: number;
  denseCommitColor?: string;
  normalCommitColor?: string;
  emptyCommitColor?: string;
}

const ContributionGraph = ({
  contributionItems,
  columnCount = 16,
  rowCount = 4,
  denseCommitColor,
  normalCommitColor,
  emptyCommitColor,
  ...props
}: Props) => {
  const theme = useContext(ThemeContext);

  const contributionItemColor = {
    dense: denseCommitColor ?? theme.color.primaryColor,
    normal: normalCommitColor ?? theme.color.secondaryColor,
    empty: emptyCommitColor ?? theme.color.tertiaryColor,
  };

  const ContributionItemList = contributionItems.map((contributionItem) => (
    <ContributionItem backgroundColor={contributionItemColor[contributionItem]} />
  ));

  return (
    <Container {...props} columnCount={columnCount} rowCount={rowCount}>
      {ContributionItemList}
    </Container>
  );
};

export default ContributionGraph;
