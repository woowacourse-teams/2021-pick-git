package com.woowacourse.pickgit.portfolio.domain.contact;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Contacts {

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Contact> contacts;

    protected Contacts() {
        this(new ArrayList<>());
    }

    public Contacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public static Contacts empty() {
        return new Contacts(new ArrayList<>());
    }

    public void appendTo(Portfolio portfolio) {
        this.contacts.forEach(contact -> contact.appendTo(portfolio));
    }

    public void update(Contacts source, Portfolio portfolio) {
        source.contacts.forEach(contact -> contact.appendTo(portfolio));

        UpdateUtil.execute(this.contacts, source.contacts);
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
