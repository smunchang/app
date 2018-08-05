package com.kaybo.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

@SpringBootApplication
public class AppApplication extends SpringBootServletInitializer {

	private static Log logger = LogFactory.getLog(AppApplication.class);

	@Bean
	protected ServletContextListener listener() {
		return new ServletContextListener() {

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				logger.info("ServletContext initialized");
			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				logger.info("ServletContext destroyed");
			}

		};
	}

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AppApplication.class);
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		//sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		//sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:/config/mybatis-config.xml"));
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
}
