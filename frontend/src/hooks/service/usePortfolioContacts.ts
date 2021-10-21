import { PortfolioContact } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import { CONTACTS } from "../../constants/portfolio";
import { setPortfolioLocalUpdateTime } from "../../storage/storage";
import useLocalStorage from "../common/useLocalStorage";

const usePortfolioContacts = (username: string) => {
  const { itemState: portfolioContacts, setItem } = useLocalStorage<PortfolioContact[]>(
    PORTFOLIO.CONTACTS(username),
    CONTACTS
  );

  const setPortfolioContacts = (contacts: PortfolioContact[], shouldRenewUpdateTime: boolean = true) => {
    shouldRenewUpdateTime && setPortfolioLocalUpdateTime(new Date());
    setItem(contacts);
  };

  const setPortfolioContact = (category: string, value: string, shouldRenewUpdateTime: boolean = true) => {
    shouldRenewUpdateTime && setPortfolioLocalUpdateTime(new Date());
    const newPortfolioContacts = [...portfolioContacts];
    const targetContact = newPortfolioContacts.find((portfolioContact) => portfolioContact.category === category);

    if (!targetContact) {
      return;
    }

    targetContact.value = value;
    setPortfolioContacts(newPortfolioContacts);
  };

  return {
    portfolioContacts,
    setPortfolioContacts,
    setPortfolioContact,
  };
};

export default usePortfolioContacts;
