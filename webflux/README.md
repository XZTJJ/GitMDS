# 介绍

jdk8,  springboot2.x, 使用响应式(webflux)的方式 进行web开发，响应式WebClient客户端，响应式 关系型数据库的方式，响应式 spring security整合 。



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

