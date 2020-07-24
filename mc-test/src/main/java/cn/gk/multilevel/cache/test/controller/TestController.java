package cn.gk.multilevel.cache.test.controller;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import cn.gk.multilevel.cache.sdk.api.MultilevelCacheable;
import cn.gk.multilevel.cache.test.model.DemoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/obj")
    @MultilevelCacheable(cacheName = "#key")
    public String test(@RequestParam String key, @RequestParam String value) {
        return value;
    }
    @PostMapping("/array")
    @MultilevelCacheable(cacheName = "demoTestPut")
    public List<DemoDTO> testArrays(@RequestParam Integer count) {
        List<DemoDTO> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            DemoDTO demoDTO=new DemoDTO();
            demoDTO.setId(i);
            demoDTO.setName("xingming"+i);
            list.add(demoDTO);
        }
        return list;
    }
    @GetMapping("")
    public String test(@RequestParam String key) {
        return mcTemplate.tryGetValue(key,String.class);
    }


}
