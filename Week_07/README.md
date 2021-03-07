# 学习笔记

## DB 与 SQL 优化（重点）

**定位问题的方法**：

1. 根据慢查询日志优化SQL，**重点查看Rank、Response time、Rows examine、min几个指标**

2. 看应用和运维的监控

   监控三要素：

   - 采集间隔
   - 指标计算方法，最大值，最小值，平均值
   - 数据来源

   读出的信息：

   - TPS和延迟异常飙升
   - 持续延迟可能导致高可用失效
   - TPS推升导致延迟升高
   - TPS达到最高点时目前架构的性能瓶颈

3. 加索引

   索引优化：

   - 尽可能用主键索引，避免回表
   - 覆盖索引
   - 最左前缀原则
   - 索引下推

**总结1：写入优化**

大批量写入的优化：

- PreparedStatement 减少 SQL 解析
- Multiple Values/Add Batch 减少交互
- Load Data，直接导入
- 索引和约束问题（去掉索引和约束写数据，写好再补上）

**总结2：数据更新**

数据的范围更新：

- 注意 GAP Lock 的问题
- 导致锁范围扩大（**如果搜索的条件未建立索引或具有非唯一索引，则该语句会锁定前面的间隙；对于使用唯一索引来锁定唯一行来锁定行的语句，不需要间隙锁。如果是多列组成的唯一索引, 并且搜索条件中仅包含部分列, 这时也会产生间隙锁。--官方原话**）

**总结3：模糊查询**

Like 的问题：

- 前缀匹配
- 否则不走索引（‘张三%’走索引，‘%张三%’不走索引）
- 全文检索（MySQL 5.7.6开始自带ngram全文解析器，缺点影响TPS）
- solr/ES

**总结4：连接查询**

连接查询优化：

- 驱动表的选择问题（选择数据量小，条件精确的表）
- 避免笛卡尔积

**总结5：索引失效**

索引失效的情况汇总：

- NULL比较，not，not in，函数等
- 减少使用 or（在没有索引和主键的列，随着or后面的数据量越多，性能下降很厉害，而in不会有太多下降；如果有索引和主键上in和or没区别），可以用 union（注意 union all 的区别）或 in，以及前面提到的like
- 大数据量下，放弃所有条件组合都走索引的幻想，出门左拐“全文检索”
- 必要时可以使用 force index 来强制查询走某个索引

**总结6：查询SQL到底怎么设计？**

- 查询数据量和查询次数的平衡
- 避免不必须的大量重复数据传输
- 避免使用临时文件排序或临时表
- 分析类需求，可以用汇总表

## 常见场景分析（重点）

**怎么实现主键ID**

- 自增
- sequence（Oracle、DB2中全局的自增ID）
- 模拟 seq（建一张表来维护全局序列，通过乐观锁或者悲观锁来自增，并发太大可以增加一个步长1000或者10000，相当于一次性取1000或10000个ID在内存操作，提高并发，缺点是如果一个步长的ID没用完，就会导致ID不连续）
- UUID（随机性好，缺点太长，计算速度慢）
- 时间戳/随机数
- snowflake（分布式ID主流，把一个ID分成三段组成，前面一段是服务器id或机房id，中间的是类似时间戳，后面一段是随机数或自增数）

**高效分页**

- 分页：count/pageSize/pageNum, 带条件的查询语句

- 常见实现-分页插件：使用查询 SQL，嵌套一个 count，性能的坑？

  改进一下1，重写分页插件的 count(*)

- 大数量级分页的问题，limit 100000,20

  改进一下2，反序

  继续改进3，技术方向：查询条件带 id，再limit 20

  继续改进4，需求方向：非精确分页

- 所有条件组合？ 索引？全文检索

**乐观锁与悲观锁**

```mysql
select * from xxx for update
update xxx
commit；
```

意味着什么？锁定范围太大，并发效率低。

```mysql
select * from xxx
update xxx where value=oldValue
```

value相当于版本号。

## MySQL 主从复制（重点）

**主从复制原理**

核心是：

1. 主库写 binlog
2. 从库 relay log

![主从复制](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/%E4%B8%BB%E4%BB%8E%E5%A4%8D%E5%88%B6.png)

历史：

- 2000年，MySQL 3.23.15版本引入了复制
- 2002年，MySQL 4.0.2版本分离 IO 和 SQL 线程，引入了 relay log
- 2010年，MySQL 5.5版本引入半同步复制
- 2016年，MySQL 在5.7.17中引入 InnoDB Group Replication

Binlog 格式

- ROW
- Statement
- Mixed

异步复制：传统主从复制--2000年，MySQL 3.23.15版本引入了 Replication

![异步复制](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/%E5%BC%82%E6%AD%A5%E5%A4%8D%E5%88%B6.png)

半同步复制：需要启用插件

![半同步复制](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/%E5%8D%8A%E5%90%8C%E6%AD%A5%E5%A4%8D%E5%88%B6.png)

组复制：

![组复制](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/%E7%BB%84%E5%A4%8D%E5%88%B6.png)

**主从复制的局限性**

1. 主从延迟问题
2. 应用侧需要配合读写分离框架
3. 不解决高可用问题

## MySQL 读写分离（重点）

[**读写分离-动态切换数据源版本1.0**](https://github.com/liaoxiangrui/JAVA-01/tree/main/Week_07/Lesson_14/work_02/switchdatasource)

1. 基于 Spring/Spring Boot，配置多个数据源(例如2个，master 和 slave)
2. 根据具体的 Service 方法是否会操作数据，注入不同的数据源,1.0版本
3. 改进一下1.1：基于操作 AbstractRoutingDataSource 和自定义注解 readOnly 之类的，简化自动切换数据源
4. 改进二下1.2：支持配置多个从库；
5. 改进三下1.3：支持多个从库的负载均衡。

[**读写分离-数据库框架版本2.0**](https://github.com/liaoxiangrui/JAVA-01/tree/main/Week_07/Lesson_14/work_03/switchdatasource2.0)

1. 分析前一版本“动态切换数据源”有什么问题？
   - 侵入性还是较强
   - 降低侵入性会导致“写完读”不一致问题
2. 改进方式，ShardingSphere-jdbc 的 Master-Slave 功能
   - SQL 解析和事务管理，自动实现读写分离
   - 解决“写完读”不一致的问题

**读写分离-数据库中间件版本3.0**

1. 分析前一版本“框架版本”有什么问题？
   - 对业务系统还是有侵入
   - 对已存在的旧系统改造不友好
2. 改进方式，MyCat/ShardingSphere-Proxy 的 Master-Slave 功能
   - 需要部署一个中间件，规则配置在中间件
   - 模拟一个 MySQL 服务器，对业务系统无侵入

## MySQL 高可用（重点）

**为什么要高可用**

1. 读写分离，提升读的处理能力
2. 故障转移，提供 failover 能力

加上业务侧连接池的心跳重试，实现断线重连，业务不间断，降低 RTO 和 RPO。

什么是 failover：故障转移，灾难恢复

容灾：热备与冷备

对于主从来说，简单讲就是主挂了，某一个从，变成主，整个集群来看，正常对外提供服务。

常见的一些策略：

1. 多个实例不在一个主机/机架上
2. 跨机房和可用区部署
3. 两地三中心容灾高可用方案

**高可用定义**

高可用意味着，更少的不可服务时间。一般用SLA/SLO衡量。

1年 = 365天 = 8760小时

99 = 8760 * 1% = 8760 * 0.01 = 87.6小时

99.9 = 8760 * 0.1% = 8760 * 0.001 = 8.76小时

99.99 = 8760 * 0.0001 = 0.876小时 = 0.876 * 60 = 52.6分钟

99.999 = 8760 * 0.00001 = 0.0876小时 = 0.0876 * 60 = 5.26分钟

**MySQL高可用0：主从手动切换**

- 如果主节点挂掉，将某个从改成主；
- 重新配置其他从节点。
- 修改应用数据源配置。

有什么问题？

1. 可能数据不一致。
2. 需要人工干预。
3. 代码和配置的侵入性。

**MySQL高可用1：主从手动切换**

- 用 LVS+Keepalived 实现多个节点的探活+请求路由。
- 配置 VIP 或 DNS 实现配置不变更。

有什么问题？

1. 手工处理主从切换
2. 大量的配置和脚本定义

**MySQL高可用2：MHA**

MHA（Master High Availability）目前在 MySQL 高可用方面是一个相对成熟的解决方案，它由日本 DeNA 公司的 youshimaton（现就职于 Facebook 公司）开发，是一套优秀的作为 MySQL 高可用性环境下故障切换和主从提升的高可用软件。基于 Perl 语言开发，一般能在30s内实现主从切换。切换时，直接通过 SSH 复制主节点的日志。

有什么问题？

1. 需要配置 SSH 信息
2. 至少3台

![MHA](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/MHA.png)

**MySQL高可用3：MGR**

如果主节点挂掉，将自动选择某个从改成主；无需人工干预，基于组复制，保证数据一致性。

有什么问题？

1. 外部获得状态变更需要读取数据库。
2. 外部需要使用 LVS/VIP 配置。

![MGR](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/MGR.png)

MGR 特点

![MGR特点](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/MGR%E7%89%B9%E7%82%B9.png)

**MySQL高可用4：MySQL Cluster**

![Cluster](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/Cluster1.png)

![Cluster](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/Cluster2.png)

**MySQL高可用5：Orchestrator**

如果主节点挂掉，将某个从改成主；

![orchestrator](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/orchestrator.png)

优势：

- 能直接在 UI 界面
- 拖拽改变主从关系

![orchestrator](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_07/Image/orchestrator1.png)