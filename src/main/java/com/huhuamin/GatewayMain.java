package com.huhuamin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/14 14:20
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayMain implements EnvironmentAware {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMain.class, args);
    }

    @Override
    public void setEnvironment(Environment environment) {
        //System.out.println(value);
        System.out.println(environment);
    }

    /**
     * 新增容器bean，GatewayProperties，支持动态刷新
     *
     * @return
     */
    @Primary
    @RefreshScope
    @Bean
    public GatewayProperties gatewayPropertiesRefresh() {
        return new GatewayProperties();
    }

}
