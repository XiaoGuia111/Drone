package com.uav.system.config.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Apache Shiro 安全框架配置
 *
 * <p>
 * 配置 Shiro 的核心组件：
 * </p>
 * <ul>
 * <li>{@link SecurityManager} — 安全管理器，Shiro 的核心</li>
 * <li>{@link ShiroRealm} — 自定义 Realm，从数据库获取用户认证/授权信息</li>
 * <li>{@link ShiroFilterFactoryBean} — 过滤器链，定义 URL 的访问权限</li>
 * </ul>
 *
 * <p>
 * <b>过滤器链规则（按顺序匹配）：</b>
 * </p>
 * <ul>
 * <li>{@code /static/**} — anon（静态资源匿名访问）</li>
 * <li>{@code /login} — anon（登录页面）</li>
 * <li>{@code /api/auth/login} — anon（登录接口）</li>
 * <li>{@code /api/**} — authc（需要登录认证）</li>
 * <li>{@code /**} — authc（其余所有页面需要认证）</li>
 * </ul>
 *
 * @see ShiroRealm
 */
@Configuration
public class ShiroConfig {

    /**
     * 创建安全管理器
     */
    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        return securityManager;
    }

    /**
     * 配置 Shiro 过滤器链
     *
     * <p>
     * 定义 URL 路径与访问权限的映射关系。
     * </p>
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        factoryBean.setSecurityManager(securityManager);
        // 未登录时跳转到登录页面
        factoryBean.setLoginUrl("/login");
        // 登录成功后跳转的首页
        factoryBean.setSuccessUrl("/");
        // 未授权时跳转的页面
        factoryBean.setUnauthorizedUrl("/login");

        // 自定义过滤器链：LinkedHashMap 保证顺序
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 静态资源放行
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");

        // 登录相关放行
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/api/auth/login", "anon");

        // 登出
        filterChainDefinitionMap.put("/logout", "logout");

        // 所有 /api/** 和页面需要认证
        filterChainDefinitionMap.put("/api/**", "authc");
        filterChainDefinitionMap.put("/uav/**", "authc");
        filterChainDefinitionMap.put("/", "authc");

        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return factoryBean;
    }

    /**
     * 启用 Shiro 注解支持（如 @RequiresRoles、@RequiresPermissions）
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * 自动代理创建器，确保 Shiro 注解生效
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
}
