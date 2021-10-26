package com.woowacourse.pickgit.exception.portfolio;

import com.woowacourse.pickgit.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class PortfolioException extends ApplicationException {

    protected PortfolioException(
        String errorCode,
        HttpStatus httpStatus,
        String message
    ) {
        super(errorCode, httpStatus, message);
    }
}
