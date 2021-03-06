package cn.edu.sicnu.cs.config;

import cn.edu.sicnu.cs.constant.IgnoredUrlsProperties;
import cn.edu.sicnu.cs.filter.*;
import cn.edu.sicnu.cs.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @Classname SecurityConfig
 * @Description TODO
 * @Date 2020/11/16 21:41
 * @Created by Huan
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected AuthenticationManager authenticationManager;
    @Autowired
    private IgnoredUrlsProperties ignoredUrlsProperties;

    @Resource
    private UserDetailsService userDetailsServiceImpl;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failHandler;

    @Autowired
    private RestAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    private CustomLogoutHandler customLogoutHandler;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;


    private static final String[] AUTH_WHITELIST = {

            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v2/*",
            "/webjars/**",
            "/swagger-ui.html",
            "/swagger-ui/*",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v1.1/login1/captcha",
            "/doc.html"
    };

//    @Autowired
//    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;


    @Bean("passwordEncoder")
    public BCryptPasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(this.getPasswordEncoder());
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        MyUsernamePasswordAuthenticationFilter myUsernamePasswordAuthenticationFilter = new MyUsernamePasswordAuthenticationFilter();
        myUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);//????????????????????????
        myUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);//????????????????????????
        myUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(failHandler); //????????????????????????

        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager);

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        //????????????????????????????????????????????????????????????????????????
        for(String url:ignoredUrlsProperties.getUrls()){
            registry.antMatchers(url).permitAll();
        }
        http
                //??????jtw???????????????
                .addFilterAt(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(myFilterSecurityInterceptor, FilterSecurityInterceptor.class)
                //??????????????????????????????
                .addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class)
                //??????????????????????????????
                .addFilterAfter(myUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors()
                .and()
                //??????csrf,????????????????????????
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(entryPointUnauthorizedHandler).accessDeniedHandler(accessDeniedHandler)
                .and()
                //??????session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/static/**","v1.1/login","/authenticate","/hello","/swagger-ui/index.html","loginsuccess.html","/v1.1/login1/captcha").permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/swagger-ui/index.html").anonymous()
                //????????????  json:/v1/login
                .anyRequest().authenticated()
                .and()
                .logout().logoutUrl("/v1.1/logout")
                        .addLogoutHandler(customLogoutHandler)
                         .logoutSuccessHandler(customLogoutSuccessHandler);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
