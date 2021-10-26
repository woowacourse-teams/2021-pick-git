import { PortfolioContact } from "../../@types";
import { CONTACT_CATEGORY_TEXT, PORTFOLIO_CONTACTS } from "../../constants/portfolio";
import Button from "../@shared/Button/Button";
import Input from "../@shared/Input/Input";
import {
  Container,
  ContactInputBlock,
  ContactInputBlockWrapper,
  ContactInputCSS,
  ContactInputCompleteButtonCSS,
  ContactLabel,
} from "./PortfolioContactForm.style";

export interface Props {
  portfolioContacts: PortfolioContact[];
  setPortfolioContact: (category: PortfolioContact["category"], value: PortfolioContact["value"]) => void;
  onEditComplete: () => void;
}

const PortfolioContactForm = ({ portfolioContacts, setPortfolioContact, onEditComplete }: Props) => {
  const handleContactInputChange: React.FormEventHandler<HTMLInputElement> = ({ currentTarget }) => {
    const { value, name } = currentTarget;
    setPortfolioContact(name, value);
  };

  return (
    <Container>
      <ContactInputBlockWrapper>
        {portfolioContacts.map((portfolioContact) => (
          <ContactInputBlock key={portfolioContact.category}>
            <ContactLabel>{CONTACT_CATEGORY_TEXT[portfolioContact.category]}</ContactLabel>
            <Input
              kind="borderBottom"
              placeholder="미기재"
              value={portfolioContact.value}
              onChange={handleContactInputChange}
              name={portfolioContact.category}
            />
          </ContactInputBlock>
        ))}
      </ContactInputBlockWrapper>
      <Button kind="roundedBlock" onClick={onEditComplete}>
        작성 완료
      </Button>
    </Container>
  );
};

export default PortfolioContactForm;
