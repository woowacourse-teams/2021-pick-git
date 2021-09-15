package com.woowacourse.pickgit.portfolio.domain.contact;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

    public List<Contact> updateContacts(List<Contact> sources) {
        updateExistingContacts(sources);
        addNonExistingContacts(sources);
        removeUselessContacts(sources);

        return contacts;
    }

    private void updateExistingContacts(List<Contact> sources) {
        for (Contact source : sources) {
            updateExistingContact(source, getContactsWithId());
        }
    }

    private void updateExistingContact(Contact source, Map<Long, Contact> contactsWithId) {
        if (contactsWithId.containsKey(source.getId())) {
            Contact target = contactsWithId.get(source.getId());

            target.updateCategory(source.getCategory());
            target.updateValue(source.getValue());
        }
    }

    private Map<Long, Contact> getContactsWithId() {
        return contacts.stream()
            .collect(toMap(Contact::getId, Function.identity()));
    }

    private void addNonExistingContacts(List<Contact> sources) {
        List<Contact> nonExistingContacts = sources.stream()
            .filter(source -> !contacts.contains(source))
            .collect(toList());
        contacts.addAll(nonExistingContacts);
    }

    private void removeUselessContacts(List<Contact> sources) {
        contacts.removeIf(contact -> !sources.contains(contact));
    }

    public void add(Contact contact) {
        contacts.add(contact);
    }


    public void remove(Contact contact) {
        contacts.remove(contact);
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
