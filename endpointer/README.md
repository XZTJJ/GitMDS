### 说明
#### 流程
​    spring-boot + websocket + 单利 + log4j2 ，工厂，责任链 + redis + restful ，使用路径变量来编码restful接口，区分不同的handler。 hanlder有一个抽象的类，定义了整体的流程，分为解析，存储 ，推送，每一个都是通过一个enum来进行匹配的。这样就可以讲解析和流程 分开了。








