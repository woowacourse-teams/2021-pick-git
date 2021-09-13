package com.woowacourse.pickgit.portfolio;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean profileImageShown;

    @Column(nullable = false)
    private String profileImageUrl;

    private String introduction;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<Contract> contracts;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<Section> sections;
}
