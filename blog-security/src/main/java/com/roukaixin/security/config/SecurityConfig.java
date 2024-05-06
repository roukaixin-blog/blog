package com.roukaixin.security.config;


import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.roukaixin.security.annotation.NoPermitLogin;
import com.roukaixin.security.authorization.filter.AuthenticationFiler;
import com.roukaixin.security.authorization.registration.JdbcClientRegistrationRepository;
import com.roukaixin.security.constant.SwaggerConstant;
import com.roukaixin.security.handler.NotAuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 自定义 security 配置
 *
 * @author 不北咪
 * @date 2023/6/30 上午11:16
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final List<AuthenticationProvider> authenticationProviders;

    private final Map<String, PasswordEncoder> encoders;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JdbcClientRegistrationRepository jdbcClientRegistrationRepository;

    private final UserDetailsService usernamePasswordUserDetailsService;

    private final UserDetailsPasswordService usernamePasswordUserDetailsPasswordService;

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService;

    @Autowired
    public SecurityConfig(List<AuthenticationProvider> authenticationProviders,
                          Map<String, PasswordEncoder> encoders,
                          RequestMappingHandlerMapping requestMappingHandlerMapping,
                          RedisTemplate<String, Object> redisTemplate,
                          JdbcClientRegistrationRepository jdbcClientRegistrationRepository,
                          UserDetailsService usernamePasswordUserDetailsService,
                          UserDetailsPasswordService usernamePasswordUserDetailsPasswordService,
                          OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService) {
        this.authenticationProviders = authenticationProviders;
        this.encoders = encoders;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.redisTemplate = redisTemplate;
        this.jdbcClientRegistrationRepository = jdbcClientRegistrationRepository;
        this.usernamePasswordUserDetailsService = usernamePasswordUserDetailsService;
        this.usernamePasswordUserDetailsPasswordService = usernamePasswordUserDetailsPasswordService;
        this.defaultOAuth2UserService = defaultOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Multimap<HttpMethod, String> noPermitLogins = getNoPermitLogins();
        return
                http
                        .sessionManagement(sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(HttpMethod.GET,
                                                noPermitLogins.get(HttpMethod.GET)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.HEAD,
                                                noPermitLogins.get(HttpMethod.HEAD)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.POST,
                                                noPermitLogins.get(HttpMethod.POST)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.PUT,
                                                noPermitLogins.get(HttpMethod.PUT)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.PATCH,
                                                noPermitLogins.get(HttpMethod.PATCH)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.DELETE,
                                                noPermitLogins.get(HttpMethod.DELETE)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.OPTIONS,
                                                noPermitLogins.get(HttpMethod.OPTIONS)
                                                        .toArray(new String[]{})).permitAll()
                                        .requestMatchers(HttpMethod.TRACE,
                                                noPermitLogins.get(HttpMethod.TRACE)
                                                        .toArray(new String[]{})).permitAll()
                                        .anyRequest().authenticated()
                        )
                        .exceptionHandling(exception ->
                                exception.authenticationEntryPoint(new NotAuthenticationHandler())
                        )
                        .addFilterBefore(
                                new AuthenticationFiler(redisTemplate, jdbcClientRegistrationRepository),
                                AuthorizationFilter.class
                        )
                        .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {};
    }

    /**
     * 获取不需要登录就能访问的接口
     */
    private Multimap<HttpMethod, String> getNoPermitLogins() {
        // 不需要登录就可以访问的接口
        Multimap<HttpMethod, String> methodNoPermitLogin = LinkedListMultimap.create();
        // 获取全部提供接口处理方法（controller的方法）
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        // 遍历所有方法
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            RequestMappingInfo key = methodEntry.getKey();
            HandlerMethod value = methodEntry.getValue();
            // 获取类上的 NoPermitLogin 注解
            NoPermitLogin annotation = value.getBeanType().getAnnotation(NoPermitLogin.class);
            if (ObjectUtils.isEmpty(annotation)) {
                // 类上面没有 NoPermitLogin 注解，获取方法上的注解
                NoPermitLogin methodAnnotation = value.getMethodAnnotation(NoPermitLogin.class);
                if (!ObjectUtils.isEmpty(methodAnnotation)) {
                    addMethodMappingUrl(methodNoPermitLogin, key);
                }
            } else {
                // 如果类上面有 NoPermitLogin 注解，那么就不管方法上有没有都是放行的
                addMethodMappingUrl(methodNoPermitLogin, key);
            }
        }
        methodNoPermitLogin.putAll(HttpMethod.GET, getSwaggerPermit());
        return methodNoPermitLogin;
    }

    /**
     * 获取文档放行的接口和静态资源
     * @return Set<String>
     */
    private Set<String> getSwaggerPermit() {
        Set<String> set = new TreeSet<>();
        set.add(SwaggerConstant.WEBJARS);
        set.add(SwaggerConstant.API_DOCS);
        set.add(SwaggerConstant.FAVICON_ICO);
        set.add(SwaggerConstant.DOC_HTML);
        return set;
    }

    /**
     * 添加 mapping url 到 map 中
     * @param methodNoPermitLogin methodNoPermitLogin
     * @param key 请求信息
     */
    private void addMethodMappingUrl(Multimap<HttpMethod, String> methodNoPermitLogin, RequestMappingInfo key) {
        // 请求方式。例如: GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        Set<RequestMethod> methods = key.getMethodsCondition().getMethods();
        // 请求 url
        Set<String> patternValues;
        if (key.getPathPatternsCondition() != null) {
            patternValues = key.getPathPatternsCondition().getPatternValues();
        } else if (key.getPatternsCondition() != null) {
            patternValues = key.getPatternsCondition().getPatterns();
        } else {
            return;
        }
        if (!methods.isEmpty()) {
            methods.forEach(method -> methodNoPermitLogin.putAll(method.asHttpMethod(), patternValues));
        } else if (!patternValues.isEmpty()){
            // 如果使用 @RequestMapping 注解映射，那么放行的时候默认只放行 get 请求
            methodNoPermitLogin.putAll(HttpMethod.GET, patternValues);
        }
    }

    /**
     * 加密方式。BCryptPasswordEncoder、Pbkdf2PasswordEncoder、SCryptPasswordEncoder
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new DelegatingPasswordEncoder(PasswordEncoderConfig.encodingId().getEncodingId(), encoders);
    }

    /**
     * 自定义 AuthenticationManager
     * @return 局部 AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        authenticationProviders.add(usernamePasswordAuthenticationProvider());
        authenticationProviders.add(oauth2AuthenticationProvider());
        return new ProviderManager(authenticationProviders);
    }

    public AuthenticationProvider usernamePasswordAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(usernamePasswordUserDetailsService);
        authenticationProvider.setUserDetailsPasswordService(usernamePasswordUserDetailsPasswordService);
        return authenticationProvider;
    }

    public AuthenticationProvider oauth2AuthenticationProvider() {
        return new OAuth2LoginAuthenticationProvider(
                new DefaultAuthorizationCodeTokenResponseClient(),
                defaultOAuth2UserService
        );
    }
}
