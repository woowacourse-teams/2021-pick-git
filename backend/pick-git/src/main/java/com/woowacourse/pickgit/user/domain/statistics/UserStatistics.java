package com.woowacourse.pickgit.user.domain.statistics;

import com.woowacourse.pickgit.user.domain.Vendor;

public class UserStatistics {

    private final Vendor vendor;
    private int commit;

    private UserStatistics(Vendor vendor) {
        this.vendor = vendor;
    }

    public int getCommit() {
        return vendor.getCommit();
    }
}
