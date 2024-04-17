package com.roukaixin.security.authorization.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 解析构建OAuth2AuthorizationRequest 对象。参考：DefaultOAuth2AuthorizationRequestResolver类
 *
 * @author 不北咪
 * @date 2024/3/6 下午3:41
 */
@Setter
@Getter
@Slf4j
public class CustomizeOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private String registrationId;

    private static final char PATH_DELIMITER = '/';

    private static final StringKeyGenerator DEFAULT_STATE_GENERATOR = new Base64StringKeyGenerator(
            Base64.getUrlEncoder());

    private static final StringKeyGenerator DEFAULT_SECURE_KEY_GENERATOR = new Base64StringKeyGenerator(
            Base64.getUrlEncoder().withoutPadding(), 96);

    private static final Consumer<OAuth2AuthorizationRequest.Builder> DEFAULT_PKCE_APPLIER = OAuth2AuthorizationRequestCustomizers
            .withPkce();

    private final ClientRegistrationRepository clientRegistrationRepository;


    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer = (customizer) -> {
    };

    public CustomizeOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                       String registrationId) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.registrationId = registrationId;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        if (this.registrationId == null) {
            return null;
        }
        String redirectUriAction = getAction(request, "login");
        return resolve(request, this.registrationId, redirectUriAction);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        if (this.registrationId == null) {
            return null;
        }
        String redirectUriAction = getAction(request, "authorize");
        return resolve(request, this.registrationId, redirectUriAction);
    }

    private String getAction(HttpServletRequest request, String defaultAction) {
        String action = request.getParameter("action");
        if (action == null) {
            return defaultAction;
        }
        return action;
    }

    private OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId,
                                               String redirectUriAction) {
        if (registrationId == null) {
            return null;
        }
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new RuntimeException("Invalid Client Registration with Id: " + registrationId);
        }
        OAuth2AuthorizationRequest.Builder builder = getBuilder(clientRegistration);

        String redirectUriStr = expandRedirectUri(request, clientRegistration, redirectUriAction);

        // @formatter:off
        builder.clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(redirectUriStr)
                .scopes(clientRegistration.getScopes())
                .state(DEFAULT_STATE_GENERATOR.generateKey());
        // @formatter:on

        this.authorizationRequestCustomizer.accept(builder);

        return builder.build();
    }


    private OAuth2AuthorizationRequest.Builder getBuilder(ClientRegistration clientRegistration) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
            // @formatter:off
            OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                    .attributes((attrs) ->
                            attrs.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
            // @formatter:on
            if (!CollectionUtils.isEmpty(clientRegistration.getScopes())
                    && clientRegistration.getScopes().contains(OidcScopes.OPENID)) {
                // Section 3.1.2.1 Authentication Request -
                // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest scope
                // REQUIRED. OpenID Connect requests MUST contain the "openid" scope
                // value.
                applyNonce(builder);
            }
            if (ClientAuthenticationMethod.NONE.equals(clientRegistration.getClientAuthenticationMethod())) {
                DEFAULT_PKCE_APPLIER.accept(builder);
            }
            return builder;
        }
        throw new IllegalArgumentException(
                "Invalid Authorization Grant Type (" + clientRegistration.getAuthorizationGrantType().getValue()
                        + ") for Client Registration with Id: " + clientRegistration.getRegistrationId());
    }

    private static String expandRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration,
                                            String action) {
        Map<String, String> uriVariables = new HashMap<>(16);
        uriVariables.put("registrationId", clientRegistration.getRegistrationId());
        // @formatter:off
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build();
        // @formatter:on
        String scheme = uriComponents.getScheme();
        uriVariables.put("baseScheme", (scheme != null) ? scheme : "");
        String host = uriComponents.getHost();
        uriVariables.put("baseHost", (host != null) ? host : "");
        // following logic is based on HierarchicalUriComponents#toUriString()
        int port = uriComponents.getPort();
        uriVariables.put("basePort", (port == -1) ? "" : ":" + port);
        String path = uriComponents.getPath();
        if (StringUtils.hasLength(path)) {
            if (path.charAt(0) != PATH_DELIMITER) {
                path = PATH_DELIMITER + path;
            }
        }
        uriVariables.put("basePath", (path != null) ? path : "");
        uriVariables.put("baseUrl", uriComponents.toUriString());
        uriVariables.put("action", (action != null) ? action : "");
        return UriComponentsBuilder.fromUriString(clientRegistration.getRedirectUri())
                .buildAndExpand(uriVariables)
                .toUriString();
    }

    private static void applyNonce(OAuth2AuthorizationRequest.Builder builder) {
        try {
            String nonce = DEFAULT_SECURE_KEY_GENERATOR.generateKey();
            String nonceHash = createHash(nonce);
            builder.attributes((attrs) -> attrs.put(OidcParameterNames.NONCE, nonce));
            builder.additionalParameters((params) -> params.put(OidcParameterNames.NONCE, nonceHash));
        }
        catch (NoSuchAlgorithmException ex) {
            log.error("",ex);
        }
    }

    private static String createHash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(value.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
