package cn.gk.multilevel.cache.test.controller;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import cn.gk.multilevel.cache.sdk.api.MultilevelCacheable;
import cn.gk.multilevel.cache.test.model.DemoDTO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
public class TestController {
    @Autowired
    private McTemplate mcTemplate;
//
//    @PostMapping("/obj")
//    @MultilevelCacheable(cacheName = "'test' + #key")
//    public String test(@RequestParam String key, @RequestParam String value) {
//        log.info("key:{},value:{}", key, value);
//        return value;
//    }
//    @PostMapping("/array")
//    @MultilevelCacheable(cacheName = "demoTestPut")
//    public List<DemoDTO> testArrays(@RequestParam Integer count) {
//        List<DemoDTO> list = new ArrayList<>(count);
//        for (int i = 0; i < count; i++) {
//            DemoDTO demoDTO=new DemoDTO();
//            demoDTO.setId(i);
//            demoDTO.setName("xingming"+i);
//            list.add(demoDTO);
//        }
//        return list;
//    }
//    @GetMapping("")
//    public String test(@RequestParam String key) {
//        return mcTemplate.tryGetValue(key,String.class);
//    }
//

    @GetMapping("/raw/obj")
    public String getObj(@RequestParam String key) {
        return mcTemplate.tryGetValue(key,String.class);
    }

    @GetMapping("/raw/arr")
    public List<String> getList(@RequestParam String key) {
        return mcTemplate.tryGetValueArrays(key,String.class);
    }

    @PostMapping("/raw/obj")
    public String setObj(@RequestParam String key,@RequestParam String val) {
         mcTemplate.putObjectIntoCache(key,val);
        return "Success";
    }

    @PostMapping("/raw/arr")
    public String setList(@RequestParam String key,@RequestParam List<String> val) {
        mcTemplate.putObjectIntoCache(key,val);
        return "Success";
    }


    @GetMapping("/aop/obj")
    @MultilevelCacheable(cacheName = "'test' + #key")
    public String getAopObj(@RequestParam String key) {
        return mcTemplate.tryGetValue(key,String.class);
    }

    @GetMapping("/aop/arr")
    @MultilevelCacheable(cacheName = "'test' + #key")
    public List<String> getAopList(@RequestParam String key) {
        return mcTemplate.tryGetValueArrays(key,String.class);
    }
}
