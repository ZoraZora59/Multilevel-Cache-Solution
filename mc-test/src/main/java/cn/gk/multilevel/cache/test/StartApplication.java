package cn.gk.multilevel.cache.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk</h4>
 * <p>启动入口</p>
 *
 * @author zora
 * @since 2020.07.16
 */
@EnableCaching
@SpringBootApplication
@ComponentScan("cn.gk.multilevel.cache")
public class StartApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class);
    }
}
