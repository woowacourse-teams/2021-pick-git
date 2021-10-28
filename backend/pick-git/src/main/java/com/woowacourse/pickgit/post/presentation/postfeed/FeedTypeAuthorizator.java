package com.woowacourse.pickgit.post.presentation.postfeed;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.post.FeedRequestUserParameterExtractionException;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeedTypeAuthorizator {

    @Before("execution(* com.woowacourse.pickgit.post.presentation.PostFeedController.readHomeFeed(..))")
    public void checkAuthorization(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        AppUser appUser = findAppUser(args);
        String type = findType(args);

        if("followings".equalsIgnoreCase(type) && appUser.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    public AppUser findAppUser(Object[] args) {
        return (AppUser) Arrays.stream(args)
            .filter(arg -> arg instanceof AppUser)
            .findAny()
            .orElseThrow(FeedRequestUserParameterExtractionException::new);
    }

    public String findType(Object[] args) {
        return (String) Arrays.stream(args)
            .filter(arg -> arg instanceof String)
            .findAny()
            .orElseThrow(FeedRequestUserParameterExtractionException::new);
    }
}
