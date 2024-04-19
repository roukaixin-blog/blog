package com.roukaixin.security.runner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.security.controller.AuthenticationController;
import com.roukaixin.security.enums.AuthenticationMethodEnum;
import com.roukaixin.security.pojo.ProviderDetails;
import com.roukaixin.security.pojo.UserInfoEndpoint;
import com.roukaixin.security.service.ClientRegistrationService;
import com.roukaixin.security.service.ProviderDetailsService;
import com.roukaixin.security.service.UserInfoEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取 spring security 内置 oauth2 的 provider details 保存到数据库
 *
 * @author 不北咪
 * @date 2024/3/19 上午11:06
 */
@Component
public class OAuth2ProviderDetailsRunner implements CommandLineRunner {

    private final String BASE_URL = "{baseUrl}";

    private final ClientRegistrationService clientRegistrationService;

    private final ProviderDetailsService providerDetailsService;

    private final UserInfoEndpointService userInfoEndpointService;

    private final ApplicationContext applicationContext;

    private final JdbcClient jdbcClient;

    @Autowired
    public OAuth2ProviderDetailsRunner(ClientRegistrationService clientRegistrationService,
                                       ProviderDetailsService providerDetailsService,
                                       UserInfoEndpointService userInfoEndpointService,
                                       ApplicationContext applicationContext,
                                       JdbcClient jdbcClient) {
        this.clientRegistrationService = clientRegistrationService;
        this.providerDetailsService = providerDetailsService;
        this.userInfoEndpointService = userInfoEndpointService;
        this.applicationContext = applicationContext;
        this.jdbcClient = jdbcClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) {

        // 从 security 内置的 OAuth2 中获取插入到数据库中
        insertCommonOAuth2Provider();

        // 修改重定向默认值
        updateClientRegistrationRedirectUri();
    }

    /**
     * 从 security 内置的 OAuth2 中获取插入到数据库中
     */
    private void insertCommonOAuth2Provider() {
        CommonOAuth2Provider[] providers = CommonOAuth2Provider.values();
        List<String> registrationIds = Arrays.stream(providers)
                .map(e -> e.name().toLowerCase())
                .toList();
        List<ProviderDetails> providerDetails = providerDetailsService.list(
                Wrappers.<ProviderDetails>lambdaQuery().in(ProviderDetails::getRegistrationId, registrationIds)
        );
        List<ProviderDetails> providerDetailsList = new ArrayList<>();
        List<UserInfoEndpoint> userInfoEndpointList = new ArrayList<>();

        for (CommonOAuth2Provider provider : providers) {
            CommonOAuth2Provider value = CommonOAuth2Provider.valueOf(provider.name());
            if ("OKTA".equals(value.name())) {
                break;
            }
            ClientRegistration build = value.getBuilder(provider.name().toLowerCase()).clientId("1").build();
            AtomicInteger flag = new AtomicInteger();
            providerDetails.forEach(e -> {
                if (e.getRegistrationId().equals(build.getRegistrationId())) {
                    e.setAuthorizationUri(build.getProviderDetails().getAuthorizationUri());
                    e.setTokenUri(build.getProviderDetails().getTokenUri());
                    e.setJwkSetUri(build.getProviderDetails().getJwkSetUri());
                    e.setIssuerUri(build.getProviderDetails().getIssuerUri());
                    e.setConfigurationMetadata(build.getProviderDetails().getConfigurationMetadata());
                    UserInfoEndpoint userInfoEndpoint = userInfoEndpointService.getById(e.getUserInfoEndpointId());
                    userInfoEndpoint.setUri(build.getProviderDetails().getUserInfoEndpoint().getUri());
                    userInfoEndpoint.setAuthenticationMethod(
                            AuthenticationMethodEnum.valueOf(
                                    build.getProviderDetails().getUserInfoEndpoint().
                                            getAuthenticationMethod().getValue().toUpperCase()
                            )
                    );
                    userInfoEndpoint.setUserNameAttributeName(build.getProviderDetails()
                            .getUserInfoEndpoint().getUserNameAttributeName());
                    providerDetailsList.add(e);
                    userInfoEndpointList.add(userInfoEndpoint);
                    flag.getAndIncrement();
                }
            });
            if (flag.get() == 0) {
                UserInfoEndpoint endpoint = UserInfoEndpoint
                        .builder()
                        .uri(build.getProviderDetails().getUserInfoEndpoint().getUri())
                        .authenticationMethod(
                                AuthenticationMethodEnum.valueOf(
                                        build.getProviderDetails().getUserInfoEndpoint()
                                                .getAuthenticationMethod().getValue().toUpperCase()
                                )
                        )
                        .userNameAttributeName(build.getProviderDetails()
                                .getUserInfoEndpoint().getUserNameAttributeName())
                        .build();
                userInfoEndpointService.save(endpoint);
                ProviderDetails details = ProviderDetails
                        .builder()
                        .registrationId(build.getRegistrationId())
                        .authorizationUri(build.getProviderDetails().getAuthorizationUri())
                        .tokenUri(build.getProviderDetails().getTokenUri())
                        .userInfoEndpointId(endpoint.getId())
                        .jwkSetUri(build.getProviderDetails().getJwkSetUri())
                        .issuerUri(build.getProviderDetails().getIssuerUri())
                        .configurationMetadata(build.getProviderDetails().getConfigurationMetadata())
                        .build();
                providerDetailsService.save(details);
            }
        }
        userInfoEndpointService.updateBatchById(userInfoEndpointList);
        providerDetailsService.updateBatchById(providerDetailsList);
        // 清除不匹配的数据
        List<ProviderDetails> list = providerDetailsService.list();
        List<UserInfoEndpoint> userInfoEndpoints = userInfoEndpointService.list(
                Wrappers.<UserInfoEndpoint>lambdaQuery().notIn(UserInfoEndpoint::getId,
                        list.stream().map(ProviderDetails::getUserInfoEndpointId).toList())
        );
        if (!userInfoEndpoints.isEmpty()) {
            userInfoEndpointService.removeBatchByIds(userInfoEndpoints.stream().map(UserInfoEndpoint::getId).toList());
        }
    }

    /**
     * 更新重定向uri默认值
     */
    private void updateClientRegistrationRedirectUri() {
        AuthenticationController bean = applicationContext.getBean(AuthenticationController.class);
        String replaceRedirectUri = getRedirectUri(bean);
        String sql = "alter table sys_client_registration alter column redirect_uri set default '" + replaceRedirectUri + "'";
        jdbcClient.sql(sql).update();
        List<com.roukaixin.security.pojo.ClientRegistration> list = clientRegistrationService.list(
                Wrappers.<com.roukaixin.security.pojo.ClientRegistration>lambdaQuery()
                        .select(
                                com.roukaixin.security.pojo.ClientRegistration::getId,
                                com.roukaixin.security.pojo.ClientRegistration::getRedirectUri
                        )
        );
        if (!list.isEmpty()) {
            list.forEach(e -> {
                String redirectUri = e.getRedirectUri();
                if (BASE_URL.equals(redirectUri.substring(0, BASE_URL.length()))) {
                    e.setRedirectUri(replaceRedirectUri);
                }
            });
            clientRegistrationService.updateBatchById(list);
        }
    }

    private static String getRedirectUri(AuthenticationController bean) {
        RequestMapping annotation = bean.getClass().getAnnotation(RequestMapping.class);
        String defaultRedirectUri = "{baseUrl}{/authentication}/{action}/oauth2/code/{registrationId}";
        String replaceRedirectUri;
        if (annotation != null) {
            String path = annotation.value()[0];
            replaceRedirectUri = defaultRedirectUri.replace("{/authentication}", path);
        } else {
            replaceRedirectUri = defaultRedirectUri.replace("{/authentication}", "");
        }
        return replaceRedirectUri;
    }
}
