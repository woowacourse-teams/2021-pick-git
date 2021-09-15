package com.woowacourse.pickgit.portfolio.domain.contact;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    public void update(Contacts source, Portfolio portfolio) {
        source.contacts.forEach(contact -> contact.appendTo(portfolio));

        UpdateUtil.execute(this.contacts, source.contacts);
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
