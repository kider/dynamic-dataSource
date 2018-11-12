package com.example.dynamic.config;

import com.example.dynamic.mybatis.extend.DynamicSqlSessionFactoryBean;
import com.example.dynamic.mybatis.pulgin.PagePlugin;
import com.example.dynamic.tools.PropertiesTools;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * SystemDataSource数据源配置
 *
 * @author kider
 */
@Configuration
public class SystemDataSourceConfig {

    private static final Logger logger = LogManager.getLogger(SystemDataSourceConfig.class);

    @Bean(name = "systemDataSource")
    public DataSource systemDataSource(Environment env) {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        Properties prop = PropertiesTools.build(env, "spring.datasource.druid.systemDB.");
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        ds.setUniqueResourceName(prop.getProperty("name"));
        ds.setPoolSize(5);
        ds.setXaProperties(prop);
        return ds;
    }


    @Bean(name = "systemSqlSessionFactoryBean")
    public DynamicSqlSessionFactoryBean systemSqlSessionFactoryBean(Environment env, @Qualifier("systemDataSource") DataSource systemDataSource, @Qualifier("pagePlugin") PagePlugin pagePlugin)
            throws Exception {
        final DynamicSqlSessionFactoryBean sessionFactoryBean = new DynamicSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(systemDataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(env.getProperty("mybatis.systemDB.mapperLocations")));
        sessionFactoryBean.setPlugins(new Interceptor[]{pagePlugin});
        sessionFactoryBean.setDataSourceBeanName("systemDataSource");
        return sessionFactoryBean;
    }

}