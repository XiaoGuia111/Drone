package com.uav.system.config;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * 数据库初始化配置
 *
 * <p>在应用启动时自动执行 {@code classpath:schema.sql} 脚本，
 * 确保 {@code users} 和 {@code uavs} 表存在，并插入默认的 admin 用户。</p>
 *
 * <p><b>注意：</b>{@code setContinueOnError(true)} 允许脚本在遇到错误时继续执行，
 * 这在首次启动表已存在时避免报错中断。</p>
 *
 * @see DataSourceInitializer
 * @see ResourceDatabasePopulator
 */
@Configuration
public class DataSourceInitializerConfig implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 创建数据源初始化器 Bean
     * <p>加载 schema.sql 脚本并在 DataSource 上执行。</p>
     *
     * @param dataSource 注入的数据源
     * @return DataSourceInitializer 实例
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Resource schemaResource = resourceLoader.getResource("classpath:schema.sql");
        populator.addScript(schemaResource);
        populator.setContinueOnError(true); // 表已存在时不中断

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}