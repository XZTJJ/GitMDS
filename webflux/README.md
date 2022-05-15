# 介绍

jdk8,  springboot2.x, 使用响应式(webflux)的方式 进行web开发，响应式WebClient客户端，响应式 关系型数据库的方式，响应式 spring security整合, 整合websocket, 从请求中获取 用户认证信息(PersonHandler的findAll()方法), 全局错误处理， 自定义返回消息格式(PersonHandler的findById()方法)



## MySQL数据库

表定义:

```mysql
-- mydb.person 数据库
CREATE TABLE `person` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `age` int(11) NOT NULL,
  `sex` bit(1) NOT NULL,
  `birthDay` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 地址表 ，通过 name 字段进行 person  和 address 进行关联
CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `addr` varchar(300) NOT NULL,
  `updatetime` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

数据:

```sql
-- address 数据
INSERT INTO address (name,province,city,addr,updatetime) values ("name1","pro1","city1","first road of street","2022-01-10 09:25:07");
INSERT INTO address (name,province,city,addr,updatetime) values ("name1","pro2","city2","second road of street","2022-01-10 09:25:07");
INSERT INTO address (name,province,city,addr,updatetime) values ("name1","pro3","city3","third road of street","2022-01-10 09:25:07");
INSERT INTO address (name,province,city,addr,updatetime) values ("name2","pro4","city4","fourth road of street","2022-01-10 09:25:07");
INSERT INTO address (name,province,city,addr,updatetime) values ("name2","pro5","city5","fifth road of street","2022-01-10 09:25:07");
INSERT INTO address (name,province,city,addr,updatetime) values ("name3","pro6","city6","seventh road of street","2022-01-10 09:25:07");
-- person 数据  
insert into person(name,age,sex,birthDay) values('name1',10, B'0','2022-01-10 09:25:07');
insert into person(name,age,sex,birthDay) values('name2',20, B'1','2022-01-10 09:25:07');
insert into person(name,age,sex,birthDay) values('name3',30, B'0','2022-01-10 09:25:07');
insert into person(name,age,sex,birthDay) values('name4',40, B'1','2022-01-10 09:25:07');
insert into person(name,age,sex,birthDay) values('name5',50, B'0','2022-01-10 09:25:07');
```



## 下面是一些参考的连接

比较详细:

https://segmentfault.com/a/1190000040222785

https://blog.csdn.net/tonydz0523/article/details/108065025

https://blog.csdn.net/get_set/article/details/79480233

webflux整合security比较详细的 : 

https://ld246.com/article/1599322291816/comment/1599371581106

webflux整合websocket的(如果和security使用的话，必须配置ws的免拦截，注意base-uri配置)

https://juejin.cn/post/6844903847287062542#heading-0

自定义返回格式的，可以看看

https://juejin.cn/post/7065479779041935367

异常处理处理:

https://blog.cnscud.com/springcloud/2021/08/09/springcloud-exceptionhandler.html

一些异步的详细资料: 

https://segmentfault.com/a/1190000021038373

https://juejin.cn/post/6844903824566517773

