package com.woowacourse.pickgit.user.infrastructure.contribution;

import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.STAR;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@Component
public class GithubContributionExtractor implements PlatformContributionExtractor {

    private final WebClient webClient;
    private final String apiBaseUrl;

    public GithubContributionExtractor(
        WebClient webClient,
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.webClient = webClient;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public void extractStars(
        String accessToken,
        String username,
        Map<ContributionCategory, Integer> bucket,
        CountDownLatch latch
    ) {
        String apiUrl = generateUrl(username);
        sendGitHubApi(accessToken, apiUrl)
            .bodyToMono(ItemDto.class)
            .subscribe(result -> {
                bucket.put(STAR, result.sum());
                latch.countDown();
            });
    }

    private String generateUrl(String username) {
        String url = apiBaseUrl + "/search/repositories?q=user:%s stars:>=1";
        return String.format(url, username);
    }

    @Override
    public void extractCount(
        ContributionCategory category,
        String restUrl,
        String accessToken,
        String username,
        Map<ContributionCategory, Integer> bucket,
        CountDownLatch latch
    ) {
        String apiUrl = generateUrl(restUrl, username);
        sendGitHubApi(accessToken, apiUrl)
            .bodyToMono(CountDto.class)
            .subscribe(result -> {
                bucket.put(category, result.getCount());
                latch.countDown();
            });
    }

    private String generateUrl(String restUrl, String username) {
        return apiBaseUrl + String.format(restUrl, username);
    }

    private ResponseSpec sendGitHubApi(String accessToken, String apiUrl) {
        return webClient.get()
            .uri(apiUrl)
            .headers(httpHeaders -> {
                httpHeaders.setBearerAuth(accessToken);
                httpHeaders.set("Accept", "application/vnd.github.cloak-preview");
            })
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError, error -> Mono.error(PlatformHttpErrorException::new));
    }
}
