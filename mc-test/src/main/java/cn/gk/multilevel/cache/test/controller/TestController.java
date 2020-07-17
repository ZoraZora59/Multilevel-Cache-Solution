package cn.gk.multilevel.cache.test.controller;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.test.controller</h4>
 * <p>测试</p>
 *
 * @author zora
 * @since 2020.07.17
 */
@RestController
public class TestController {
    @Autowired
    private McTemplate mcTemplate;

    @PostMapping("")
    public String test(@RequestParam String key, @RequestParam String value) {
        mcTemplate.putObjectIntoCache(key, value);
        return value;
    }
    @GetMapping("")
    public String test(@RequestParam String key) {
        return mcTemplate.tryGetValue(key,String.class);
    }


}
