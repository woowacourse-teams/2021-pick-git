import Input from "../@shared/Input/Input";
import {
  Container,
  ContactInputBlock,
  ContactInputCSS,
  ContactInputCompleteButtonCSS,
  ContactLabel,
} from "./PortfolioContactForm.style";

export interface Props {}

const PortfolioContactForm = ({}: Props) => {
  return (
    <Container>
      <ContactInputBlock>
        <ContactLabel>휴대폰 연락처</ContactLabel>
        <Input kind="borderBottom" />
      </ContactInputBlock>
      <ContactInputBlock>
        <ContactLabel>이메일</ContactLabel>
        <Input kind="borderBottom" />
      </ContactInputBlock>
      <ContactInputBlock>
        <ContactLabel>포트폴리오 웹사이트</ContactLabel>
        <Input kind="borderBottom" />
      </ContactInputBlock>
      <ContactInputBlock>
        <ContactLabel>이력서 링크</ContactLabel>
        <Input kind="borderBottom" />
      </ContactInputBlock>
    </Container>
  );
};

export default PortfolioContactForm;
