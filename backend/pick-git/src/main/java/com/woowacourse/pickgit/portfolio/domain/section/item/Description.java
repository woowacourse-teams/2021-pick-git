package com.woowacourse.pickgit.portfolio.domain.section.item;

import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Description {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    protected Description() {
    }

    public Description(Long id, String value, Item item) {
        this.id = id;
        this.value = value;
        this.item = item;
    }

    public void updateValue(String value) {
        this.value = value;
    }

    public void addItem(Item item) {
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Item getItem() {
        return item;
    }
}
