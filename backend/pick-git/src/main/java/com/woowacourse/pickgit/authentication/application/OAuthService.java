package com.woowacourse.pickgit.authentication.application;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.dao.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {

    private OAuthClient githubOAuthClient;
    private JwtTokenProvider jwtTokenProvider;
    private OAuthAccessTokenDao authAccessTokenDao;
    private UserRepository userRepository;

    public OAuthService(OAuthClient githubOAuthClient,
        JwtTokenProvider jwtTokenProvider,
        OAuthAccessTokenDao authAccessTokenDao,
        UserRepository userRepository) {
        this.githubOAuthClient = githubOAuthClient;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authAccessTokenDao = authAccessTokenDao;
        this.userRepository = userRepository;
    }

    public String getGithubAuthorizationUrl() {
        return githubOAuthClient.getLoginUrl();
    }

    public String createToken(String code) {
        String githubAccessToken = githubOAuthClient.getAccessToken(code);

        OAuthProfileResponse githubProfileResponse = getGithubProfile(githubAccessToken);

        updateUserOrCreateUser(githubProfileResponse);

        return createTokenAndSave(githubAccessToken, githubProfileResponse.getName());
    }

    private void updateUserOrCreateUser(OAuthProfileResponse githubProfileResponse) {
        GithubProfile latestGithubProfile = new GithubProfile(
            githubProfileResponse.getGithubUrl(),
            githubProfileResponse.getCompany(),
            githubProfileResponse.getLocation(),
            githubProfileResponse.getWebsite(),
            githubProfileResponse.getTwitter()
        );

        userRepository.findUserByBasicProfile_Name(githubProfileResponse.getName())
            .ifPresentOrElse(user -> {
                user.setGithubProfile(latestGithubProfile);
                userRepository.save(user);
            }, () -> {
                BasicProfile basicProfile = new BasicProfile(
                    githubProfileResponse.getName(),
                    githubProfileResponse.getImage(),
                    githubProfileResponse.getDescription()
                );
                User user = new User(basicProfile, latestGithubProfile);
                userRepository.save(user);
            });
    }

    private String createTokenAndSave(String githubAccessToken, String payload) {
        String token = jwtTokenProvider.createToken(payload);
        authAccessTokenDao.insert(token, githubAccessToken);
        return token;
    }

    private OAuthProfileResponse getGithubProfile(String githubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Bearer " + githubAccessToken);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate
            .exchange("https://api.github.com/user", HttpMethod.GET, httpEntity, OAuthProfileResponse.class)
            .getBody();
    }
}
