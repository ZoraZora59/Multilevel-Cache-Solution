# multilevel-cache-solution
多级缓存解决方案

***Repo重启，准备进行大幅重构，脱离原有公司框架，全面开源***

------
![v2设计](v2设计.png)
## 概况

一级缓存：内存
二级缓存：Redis

项目的目标是将redis中的热key（热点数据）加载到一级缓存内，减轻高频请求同一个redis实例的热点数据时的压力，同时提高热点数据的访问效率。

## 实现方式

通过计数器和LRU淘汰算法，将高频访问的redis数据加载到一级缓存，同时限制缓存空间的大小，避免对服务内正常业务造成影响。

## ~~未完...可能不续~~

~~后续代码结合了公司项目代码，不方便开源，~~有兴趣可以提issue讨论

---
测试类需要在有Redis的情况下运行