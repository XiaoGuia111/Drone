package com.uav.system.config.shiro;

import com.uav.system.domain.entity.User;
import com.uav.system.repository.mapper.UserMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

/**
 * Shiro 自定义 Realm — 用户认证与授权
 *
 * <p>
 * 从数据库读取用户信息进行身份验证，并根据用户角色授予对应权限。
 * </p>
 *
 * @see UserMapper
 * @see User
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    private final UserMapper userMapper;

    public ShiroRealm(UserMapper userMapper) {
        this.userMapper = userMapper;
        // 自定义密码匹配器：处理 UsernamePasswordToken 的 credentials 是 char[] 类型的情况
        // SimpleCredentialsMatcher 默认调用 String.equals(char[]) 会返回 false
        setCredentialsMatcher(new CredentialsMatcher() {
            @Override
            public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
                String tokenCredentials = new String((char[]) token.getCredentials());
                String accountCredentials = (String) info.getCredentials();
                return tokenCredentials.equals(accountCredentials);
            }
        });
    }

    /**
     * 授权：根据用户角色分配权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        User user = userMapper.findByUsername(username);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (user != null && user.getRole() != null) {
            info.addRole(user.getRole());
        }
        return info;
    }

    /**
     * 认证：验证用户名密码
     *
     * <p>
     * 从数据库加载用户信息，返回 SimpleAuthenticationInfo 后由 CredentialsMatcher 自动比对密码。
     * UsernamePasswordToken.getCredentials() 返回 char[]，需要自定义密码匹配器。
     * </p>
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UnknownAccountException("Invalid username or password");
        }

        return new SimpleAuthenticationInfo(
                user.getUsername(),
                user.getPassword(),
                getName());
    }
}
