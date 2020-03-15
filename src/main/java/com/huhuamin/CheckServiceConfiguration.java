package com.huhuamin;

import com.huhuamin.jedis.RedisService;
import com.huhuamin.service.ICheckRange;
import com.huhuamin.service.check.impl.AllCheckRangeService;
import com.huhuamin.service.check.impl.AppCheckRangeService;
import com.huhuamin.service.check.impl.AuthCheckService;
import com.huhuamin.service.check.impl.WebCheckRangeService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 14:45
 * @Description: {@link com.huhuamin.service.ICheckRange} 的子类不是都要注册进来，不同的场景，使用不同的校验方法
 */
@Configuration
public class CheckServiceConfiguration {
    @Bean
    @Primary
    @ConditionalOnProperty(name = "sys.range", havingValue = "app")
    public ICheckRange appRange(AuthCheckService authCheckService) {
        return new AppCheckRangeService(authCheckService);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "sys.range", havingValue = "all")
    public ICheckRange allRange(AuthCheckService authCheckService) {
        return new AllCheckRangeService(authCheckService);
    }

    @Bean
    @RefreshScope
    public AuthCheckService authCheckService(ObjectProvider<RedisService> redisService) {
        return new AuthCheckService(redisService);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ICheckRange webRange(AuthCheckService authCheckService) {
        return new WebCheckRangeService(authCheckService);
    }
}
