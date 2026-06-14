package com.uav.system.config.shiro;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Shiro 测试配置
 *
 * <p>替换生产环境的 Shiro 配置，使用简单账户 Realm 和匿名 Subject，
 * 使得 {@code @WebMvcTest} 中不需要真实认证即可访问受保护端点。</p>
 */
@TestConfiguration
public class TestShiroConfig {

    @Bean
    @Primary
    public SimpleAccountRealm simpleAccountRealm() {
        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.addAccount("admin", "admin123", "ADMIN");
        return realm;
    }

    @Bean
    @Primary
    public DefaultSecurityManager securityManager(SimpleAccountRealm realm) {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(realm);
        // 绑定到当前线程，使 SecurityUtils.getSubject() 可用
        ThreadContext.bind(securityManager);
        return securityManager;
    }
}
