package com.uav.system.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类
 *
 * <p>提供 JWT（JSON Web Token）的生成、解析和验证功能。
 * 使用 HMAC-SHA512 算法对 Token 进行签名。</p>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>密钥在每次应用重启时重新生成（{@link io.jsonwebtoken.security.Keys#secretKeyFor}），
 *       重启后所有已签发的 Token 将失效。生产环境应使用固定密钥或密钥管理服务。</li>
 *   <li>Token 默认过期时间为 24 小时（86400000 毫秒），可通过修改 {@link #expiration} 调整。</li>
 *   <li>当前为无状态认证，Token 签发后无法服务端主动撤销。</li>
 * </ul>
 *
 * <p><b>性能优化：</b>Token 验证时每次都需要进行签名计算，
 * 可以考虑引入本地缓存对频繁校验的 Token 做短期缓存。</p>
 *
 * @see com.uav.system.filter.JwtAuthenticationFilter
 * @see com.uav.system.config.JwtFilterConfig
 */
@Component
public class JwtUtil {

    /**
     * HMAC-SHA512 密钥（每次应用重启重新生成）
     * <p>注意：重启后所有已签发的 Token 将失效。</p>
     */
    private final SecretKey secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);

    /** Token 过期时间（毫秒），默认 24 小时 */
    private long expiration = 86400000;

    /**
     * 生成 JWT Token
     * <p>将用户名作为 Token 主题（Subject），附带签发时间和过期时间。</p>
     *
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     *
     * @param token JWT Token
     * @return Token 中存储的用户名
     */
    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 校验 JWT Token 是否有效
     * <p>验证签名是否正确以及 Token 是否已过期。
     * 任何解析异常均视为 Token 无效。</p>
     *
     * @param token JWT Token
     * @return true 有效；false 无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
