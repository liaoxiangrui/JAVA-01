# 学习笔记

## Java8 Lambda（重点）

**什么是Lambda表达式？**

Lambda 表达式（lambda expression）是一个匿名函数，Lambda 表达式基于数学中的λ演算得名，直接对应于其中的 lambda 抽象（lambda abstraction），是一个匿名函数，即没有函数名的函数。

**Java Lambda表达式**

```java
// 1. 不需要参数,返回值为 5
() -> 5

// 2. 接收一个参数(数字类型),返回其2倍的值
x -> 2 * x

// 3. 接受2个参数(数字),并返回他们的差值
(x, y) -> x – y

// 4. 接收2个int型整数,返回他们的和
(int x, int y) -> x + y

// 5. 接受一个 string 对象,并在控制台打印,不返回任何值(看起来像是返回 void)
(String s) -> System.out.print(s)
```

## Java8 Stream（重点）

**什么是流**

Stream（流）是一个来自数据源的元素队列并支持聚合操作

- 元素：特定类型的对象，形成一个队列。 Java 中的 Stream 并不会存储元素，而是按需计算。
- 数据源：流的来源。 可以是集合，数组，I/O channel， 产生器 generator 等。
- 聚合操作 类似 SQL 语句一样的操作， 比如 filter, map, reduce, find, match, sorted 等。

和以前的 Collection 操作不同， Stream 操作还有两个基础的特征：

- Pipelining：中间操作都会返回流对象本身。 这样多个操作可以串联成一个管道， 如同流式风格(fluent style)。 这样做可以对操作进行优化， 比如延迟执行(laziness)和短路((short-circuiting)。 
- 内部迭代：以前对集合遍历都是通过 Iterator 或者 For-Each 的方式, 显式的在集合外部进行迭代， 这叫做外部迭代。 Stream 提供了内部迭代的方式， 通过访问者模式(Visitor)实现。

**Stream操作**

中间操作：

1. 选择与过滤

   filter(Predicate p) 接收 Lambda ， 从流中排除某些元素。

   distinct() 筛选，通过流所生成元素的 hashCode() 和 equals() 去除重复元素。

   limit(long maxSize) 截断流，使其元素不超过给定数量。

   skip(long n) 跳过元素，返回一个扔掉了前 n 个元素的流。若流中元素不足 n 个，则返回一个空流。

2. 映射

   map(Function f) 接收 Lambda ，将元素转换成其他形式或提取信息；接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。

   mapToDouble(ToDoubleFunction f) 接收一个函数作为参数，该函数会被应用到每个元素上，产生一个新的 DoubleStream。

   mapToInt(ToIntFunction f) 接收一个函数作为参数，该函数会被应用到每个元素上，产生一个新的 IntStream。

   mapToLong(ToLongFunction f) 接收一个函数作为参数，该函数会被应用到每个元素上，产生一个新的 LongStream。

   flatMap(Function f) 接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流。

3. 排序

   sorted() 产生一个新流，其中按自然顺序排序

   sorted(Comparator comp) 产生一个新流，其中按比较器顺序排序

终止操作：

1. 查找与匹配

   allMatch——检查是否匹配所有元素

   anyMatch——检查是否至少匹配一个元素

   noneMatch——检查是否没有匹配的元素

   findFirst——返回第一个元素

   findAny——返回当前流中的任意元素

   count——返回流中元素的总个数

   max——返回流中最大值

   min——返回流中最小值

2. 归约 reduce, 需要初始值（类比Map-Reduce）

3. 收集 collect

   toList List<T> 把流中元素收集到 List

   toSet Set<T> 把流中元素收集到 Set

   toCollection Collection<T> 把流中元素收集到创建的集合

   count 计算流中元素的个数

   summaryStatistics 统计最大最小平均值

4. 迭代 forEach

## Guava（了解）

**什么是** **Guava**

Guava 是什么?

Guava 是一种基于开源的 Java 库，其中包含谷歌正在由他们很多项目使用的很多核心库。这个库是为了方便编码，并减少编码错误。这个库提供用于集合，缓存，支持原语，并发性，常见注解，字符串处理，I/O 和验证的实用方法。

Guava 的好处

标准化 – Guava 库是由谷歌托管。

高效 - 可靠，快速和有效的扩展 JAVA 标准库。

优化 –Guava 库经过高度的优化。

JDK8 里的一些新特性源于 Guava。

**集合[Collections]**

Guava 对 JDK 集合的扩展，这是 Guava 最成熟和为人所知的部分

1. 不可变集合: 用不变的集合进行防御性编程和性能提升。
2. 新集合类型: multisets, multimaps, tables, bidirectional maps 等
3. 强大的集合工具类: 提供 java.util.Collections 中没有的集合工具
4. 扩展工具类：让实现和扩展集合类变得更容易，比如创建 Collection 的装饰器，或实现迭代器

**缓存[Caches]**

本地缓存实现，支持多种缓存过期策略

**并发[Concurrency]**

ListenableFuture：完成后触发回调的 Future

**字符串处理[Strings]**

非常有用的字符串工具，包括分割、连接、填充等操作

**事件总线[EventBus]**

发布-订阅模式的组件通信，进程内模块间解耦

**反射[Reflection]**

Guava 的 Java 反射机制工具类

## 设计原则（重点）

**面向对象设计原则** **SOLID**

S.O.L.I.D 是面向对象设计和编程(OOD&OOP)中几个重要编码原则(Programming Priciple)的首字母缩写。

1. SRP：The Single Responsibility Principle 单一责任原则
2. OCP：The Open Closed Principle 开放封闭原则
3. LSP：The Liskov Substitution Principle 里氏替换原则
4. ISP：The Interface Segregation Principle 接口分离原则
5. DIP：The Dependency Inversion Principle 依赖倒置原则

**编码规范、checkstyle**

常见的编码规范：

1. Google 编码规范：[https://google.github.io/styleguide/javaguide.html](https://google.github.io/styleguide/javaguide.html)
2. Alibaba 编码规范：[https://github.com/alibaba/p3c](https://github.com/alibaba/p3c)
3. VIP 规范：[https://vipshop.github.io/vjtools/#/standard/](https://vipshop.github.io/vjtools/#/standard/)

## 设计模式（重点）

**GoF 23设计模式**

**创建型**

1. Factory Method（工厂方法）
2. Abstract Factory（抽象工厂）
3. Builder（建造者）
4. Prototype（原型）
5. Singleton（单例）

**结构型**

1. Adapter（适配器）
2. Bridge（桥接）
3. Composite（组合）
4. Decorator（装饰）
5. Facade（外观）
6. Flyweight（享元）
7. Proxy（代理）

**行为型**

1. Interpreter（解释器）
2. Template Method（模板方法）
3. Chain of Responsibility（责任链）
4. Command（命令）
5. Iterator（迭代器）
6. Mediator（中介者）
7. Memento（备忘录）
8. Observer（观察者）
9. State（状态）
10. Strategy（策略）
11. Visitor（访问者）

设计模式详解：[https://github.com/kimmking/design-pattern-java](https://github.com/kimmking/design-pattern-java)

图说设计模式：[https://github.com/kimmking/design_patterns](https://github.com/kimmking/design_patterns)

## 单元测试（重点）

**如何做单元测试**

1. 单元测试方法应该每个方法是一个 case，断言充分，提示明确
2. 单测要覆盖所有的 corner case
3. 充分使用 mock（一切皆可 mock）
4. 如果发现不好测试，则说明业务代码设计存在问题，可以反向优化代码
5. 批量测试用例使用参数化单元测试
6. 注意测试是单线程执行
7. 合理使用 before, after, setup 准备环境
8. 合理使用通用测试基类
9. 配合 checkstyle，coverage 等工具
10. 制定单元测试覆盖率基线

**单元测试的常见陷阱与经验**

1. 尽量不要访问外部数据库等外部资源
2. 如果必须用数据库考虑用嵌入式 DB+ 事务自动回滚
3. 防止静态变量污染导致测试无效
4. 小心测试方法的顺序导致的不同环境测试失败
5. 单元测试总时间特别长的问题

## 关系数据库 MySQL（重点）

**什么是关系数据库**

数据库设计范式

第一范式（1NF）：关系 R 属于第一范式，当且仅当R中的每一个属性A的值域只包含原子项（每个列都是原子的。）

第二范式（2NF）：在满足 1NF 的基础上，消除非主属性对码的部分函数依赖（每个列都有主键。）

第三范式（3NF）：在满足 2NF 的基础上，消除非主属性对码的传递函数依赖（从表只引用主表的主键，即表中每列都和主键相关。）

BC 范式（BCNF）：在满足 3NF 的基础上，消除主属性对码的部分和传递函数依赖

第四范式（4NF）：消除非平凡的多值依赖

第五范式（5NF）：消除一些不合适的连接依赖

**SQL** **语言**

结构化查询语言包含 6 个部分：

1. 数据查询语言（DQL: Data Query Language）：其语句，也称为“数据检索语句”，用以从表中获得数据，确定数据怎样在应用程序给出。保留字 SELECT 是 DQL（也是所有 SQL）用得最多的动词，其他 DQL 常用的保留字有 WHERE，ORDER BY，GROUP BY 和 HAVING。这些 DQL 保留字常与其它类型的 SQL 语句一起使用。
2. 数据操作语言（DML：Data Manipulation Language）：其语句包括动词 INSERT、UPDATE 和 DELETE。它们分别用于添加、修改和删除。
3. 事务控制语言（TCL）：它的语句能确保被 DML 语句影响的表的所有行及时得以更新。包括COMMIT（提交）命令、SAVEPOINT（保存点）命令、ROLLBACK（回滚）命令。
4. 数据控制语言（DCL）：它的语句通过 GRANT 或 REVOKE 实现权限控制，确定单个用户和用户组对数据库对象的访问。某些 RDBMS 可用 GRANT 或 REVOKE 控制对表单个列的访问。
5. 数据定义语言（DDL）：其语句包括动词 CREATE,ALTER 和 DROP。在数据库中创建新表或修改、删除表（CREAT TABLE 或 DROP TABLE）；为表加入索引等。
6. 指针控制语言（CCL）：它的语句，像 DECLARE CURSOR，FETCH INTO 和 UPDATE WHERE CURRENT 用于对一个或多个表单独行的操作。

## 深入数据库原理（重点）

**MySQL架构图**

![MySQL架构图](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_06/Image/MySQL%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

**MySQL存储**

独占模式

1. 日志组文件：ib_logfile0和ib_logfile1，默认均为5M
2. 表结构文件：*.frm
3. 独占表空间文件：*.ibd
4. 字符集和排序规则文件：db.opt
5. binlog 二进制日志文件：记录主数据库服务器的 DDL 和 DML 操作
6. 二进制日志索引文件：master-bin.index

共享模式 innodb_file_per_table=1

1. 数据都在 ibdata1

**MySQL简化执行流程**

![MySQL简化执行流程](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_06/Image/MySQL%E7%AE%80%E5%8C%96%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B.png)

**MySQL详细执行流程**

![MySQL详细执行流程](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_06/Image/MySQL%E8%AF%A6%E7%BB%86%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B.png)

**MySQL执行引擎和状态**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_06/Image/MySQL%E5%BC%95%E6%93%8E.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_06/Image/MySQL%E7%8A%B6%E6%80%81.png)

**MySQL对SQL执行顺序**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_06/Image/MySQL%E5%AF%B9SQL%E6%89%A7%E8%A1%8C%E9%A1%BA%E5%BA%8F.png)

实际上这个过程也并不是绝对这样的，中间 mysql 会有部分的优化以达到最佳的优化效果，比如在 select 筛选出找到的数据集

**MySQL索引原理**

数据是按页来分块的，当一个数据被用到时，其附近的数据也通常会马上被使用。InnoDB 使用 B+ 树实现聚集索引

为什么一般单表数据不超过2000万？在InnoDB默认页大小16k的情况下，按照一行数据1k的假设，一页一共16行，一般期望在B+树聚集索引3层树结构以内能找到需要的数据，主键为bigint类型的时候一个数据是8字节，InnoDB默认指针大小为6字节，所以通过计算`(16*1024/(8+6))^2*16≈2000w`，因为2000w的数据已经有3层B+树了，数据再多需要再增加层数，树的层级增加会增加IO操作，降低性能。

## MySQL 配置优化（重点）

**参数配置优化**

连接请求的变量

1. max_connections
2. back_log
3. wait_timeout和interative_timeout

查看参数配置

\- show variables like xxx

\- select @@last_insert_id;

缓冲区变量

1. key_buffer_size
2. query_cache_size（查询缓存简称 QC)
3. max_connect_errors
4. sort_buffer_size
5. max_allowed_packet=32M
6. join_buffer_size=2M
7. thread_cache_size=300

配置 Innodb 的几个变量

1. innodb_buffer_pool_size
2. innodb_flush_log_at_trx_commit
3. innodb_thread_concurrency=0
4. innodb_log_buffer_size
5. innodb_log_file_size=50M
6. innodb_log_files_in_group=3
7. read_buffer_size=1M
8. read_rnd_buffer_size=16M
9. bulk_insert_buffer_size=64M
10. binary log

## 数据库设计优化（重点）

**MySQL数据库设计优化-最佳实践**

1. 如何恰当选择引擎？默认用InnoDB；如果不需要锁，不需要用事务的业务可以用MyISAM；如果有部分表是静态数据，生成后就不变了，可以用Memory放在内存中来操作；如果是归档类型的数据表可以用archive，可以支持压缩。
2. 库表如何命名？尽可能统一用业务英文命名，统一大小写，定义命名规范形成一个字典，所有的命名在字典里去找，没有的再特别定义。
3. 如何合理拆分宽表？如果有大数据平台、ES，导过去处理；或者分一个从库出来做分析处理，跟业务分开；从库也没有的话分一些中间表出来做预处理。
4. 如何选择恰当数据类型：明确、尽量小，减小磁盘和内存压力
5. char、varchar 的选择：定长的字段用char，不确定长度用varchar
6. （text/blob/clob）的使用问题？尽可能不用，会导致页数据变少，B+树层级变多，性能变差
7. 文件、图片是否要存入到数据库？不建议存，用文件服务器代替
8. 时间日期的存储问题？数据库时间类型（有时区问题）；字符串（不利于检索，只保存到天可以使用）；时间戳（不利于人来读）
9. 数值的精度问题？精度很高的情况可以用多个字段来分开存储一个数据
10. 是否使用外键、触发器？不建议使用，影响对单表操作
11. 唯一约束和索引的关系？唯一约束一定是索引，反过来不一定
12. 是否可以冗余字段？按照范式要求默认不可以冗余，但是实际设计根据业务查询方便可以适当冗余
13. 是否使用游标、变量、视图、自定义函数、存储过程？不建议使用
14. 自增主键的使用问题？非分布式场景可以使用，分布式不使用
15. 能够在线修改表结构（DDL 操作）？在从库尝试；在凌晨没有用户量的时候停机操作；在某次系统更新上线的时候补上
16. 逻辑删除还是物理删除？通常是逻辑删除
17. 要不要加 create_time,update_time 时间戳？需要加
18. 数据库碎片问题？压缩库表来解决，压缩的时候会锁表，对性能有影响，找时间窗口尽量在用户量少的时候操作
19. 如何快速导入导出、备份数据？JDBC；JDBC批量；用数据库自己的导入导出工具，MySQL dump

