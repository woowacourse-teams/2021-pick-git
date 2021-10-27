package com.woowacourse.pickgit.post.presentation.postfeed;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeedTypeAuthorizator {

    private final String PARAMETER_EXTRACTION_ERROR_MESSAGE = "유저 관련 파라메터 추출 오류";

    @Before("execution(* com.woowacourse.pickgit.post.presentation.PostFeedController.readHomeFeed(..))")
    public void checkAuthorization(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        AppUser appUser = findAppUser(args);
        String type = findType(args);

        if(type.equalsIgnoreCase("following") && appUser.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    public AppUser findAppUser(Object[] args) {
        return (AppUser) Arrays.stream(args)
            .filter(arg -> arg instanceof AppUser)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(PARAMETER_EXTRACTION_ERROR_MESSAGE));
    }

    public String findType(Object[] args) {
        return (String) Arrays.stream(args)
            .filter(arg -> arg instanceof String)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(PARAMETER_EXTRACTION_ERROR_MESSAGE));
    }
}
