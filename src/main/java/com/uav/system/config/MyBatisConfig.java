package com.uav.system.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis 持久层框架配置
 *
 * <p>配置 MyBatis 的核心行为：</p>
 * <ul>
 *   <li>扫描 {@code classpath:mapper/*.xml} 下的 SQL 映射文件</li>
 *   <li>配置实体类别名包，使 Mapper XML 中可以直接使用类名（如 {@code Uav}）无需全路径</li>
 *   <li>启用下划线到驼峰的自动映射（如 {@code serial_number} → {@code serialNumber}）</li>
 *   <li>注册自定义类型处理器（{@link LocalDateTypeHandler}、{@link LocalDateTimeTypeHandler}）
 *       以支持 Java 8 日期时间类型与 SQLite 字符串格式之间的互转</li>
 * </ul>
 *
 * @see LocalDateTypeHandler
 * @see LocalDateTimeTypeHandler
 */
@org.springframework.context.annotation.Configuration
public class MyBatisConfig {

    /**
     * 创建 MyBatis SqlSessionFactory
     *
     * @param dataSource 数据源
     * @return SqlSessionFactory 实例
     * @throws Exception 加载映射文件失败时抛出
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );
        sessionFactory.setTypeAliasesPackage("com.uav.system.domain.entity");

        Configuration configuration = new Configuration();
        // 启用下划线转驼峰：如数据库列 created_at 映射为实体字段 createdAt
        configuration.setMapUnderscoreToCamelCase(true);

        // 注册 Java 8 日期类型处理器（SQLite 不支持原生日期类型）
        configuration.getTypeHandlerRegistry().register(LocalDateTypeHandler.class);
        configuration.getTypeHandlerRegistry().register(LocalDateTimeTypeHandler.class);

        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}