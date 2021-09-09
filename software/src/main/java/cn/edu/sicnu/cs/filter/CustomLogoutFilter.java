package cn.edu.sicnu.cs.filter;

import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.server.authentication.logout.LogoutWebFilter;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Classname CustomLogoutFilter
 * @Description TODO
 * @Date 2020/12/15 10:54
 * @Created by Huan
 */
public class CustomLogoutFilter extends LogoutWebFilter {
    public CustomLogoutFilter() {
        super();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return super.filter(exchange, chain);
    }

    @Override
    public void setLogoutSuccessHandler(ServerLogoutSuccessHandler logoutSuccessHandler) {
        super.setLogoutSuccessHandler(logoutSuccessHandler);
    }

    @Override
    public void setLogoutHandler(ServerLogoutHandler logoutHandler) {
        super.setLogoutHandler(logoutHandler);
    }

    @Override
    public void setRequiresLogoutMatcher(ServerWebExchangeMatcher requiresLogoutMatcher) {
        super.setRequiresLogoutMatcher(requiresLogoutMatcher);
    }
}
