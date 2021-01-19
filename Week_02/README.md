# 学习笔记

## GC 日志解读与分析（重点）

打印GC日志：`java -Xmx2g -Xms2g -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

分析工具：GCeasy、GCViewer

## JVM 线程堆栈数据分析（了解）

**JVM 内部线程主要分为以下几种：**

- VM 线程：单例的 VMThread 对象，负责执行 VM 操作， 下文将对此进行讨论; 
- 定时任务线程：单例的 WatcherThread 对象， 模拟在 VM 中执行定时操作的计时器中断； 
- GC 线程：垃圾收集器中，用于支持并行和并发垃圾回收 的线程; 
- 编译器线程： 将字节码编译为本地机器代码; 
- 信号分发线程：等待进程指示的信号，并将其分配给 Java 级别的信号处理方法。

**安全点：**

1. 方法代码中被植入的安全点检测入口；
2. 线程处于安全点状态：线程暂停执行，这个时候线程栈不再发生改变；
3. JVM的安全点状态：所有线程都处于安全点状态。

**JVM 支持多种方式来进行线程转储：**

1. JDK 工具, 包括: jstack 工具, jcmd 工具, jconsole, jvisualvm, Java Mission Control 等； 

2. Shell 命令或者系统控制台, 比如 Linux 的 kill -3, Windows 的 Ctrl + Break 等； 

3. JMX 技术， 主要是使用 ThreadMxBean。

分析工具：fastthread

## 内存分析与相关工具（重点）

**对象头和对象引用** 

在64位 JVM 中，对象头占据的空间是 12- byte(=96bit=64+32)，但是以8字节对齐，所以一 个空类的实例至少占用16字节。 

在32位 JVM 中，对象头占8个字节，以4的倍数对齐(32=4*8)。 

所以 new 出来很多简单对象，甚至是 new Object()，都会占用不少内容哈。 

通常在32位 JVM，以及内存小于 -Xmx32G 的64 位JVM 上(默认开启指针压缩)，一个引用占的内存 默认是4个字节。 

因此，64位 JVM 一般需要多消耗堆内存。

**包装类型** 

比原生数据类型消耗的内存要多，详情可以参考JavaWorld ： 

Integer：占用16字节(头部8+4=12，数据4字节)，因 为 int 部分占4个字节。 所以使用 Integer 比原生类 型 int 要多消耗 300% 的内存。 

Long：一般占用24个字节(头部8+4+数据8=20字 节，再对齐)，当然，对象的实际大小由底层平台的内 存对齐确定，具体由特定 CPU平台的 JVM 实现决 定。 看起来一个 Long 类型的对象，比起原生类型long 多占用了8个字节（也多消耗200%）。

多维数组：在二维数组 int[dim1][dim2] 中，每个 嵌套的数组 int[dim2] 都是一个单独的 Object， 会额外占用16字节的空间。当数组维度更大时， 这种开销特别明显。 

int[128][2] 实例占用3600字节。 而 int[256] 实 例则只占用1040字节。里面的有效存储空间是一 样的，3600 比起1040多了246%的额外开销。在 极端情况下，byte[256][1]，额外开销的比例是19 倍! 

String: String 对象的空间随着内部字符数组的增 长而增长。当然，String 类的对象有24个字节的额外开销。 

对于10字符以内的非空 String，增加的开销比起有效载荷（每个字符2字节 + 4 个字节的 length），多占用了100% 到 400% 的内存。

内存 Dump 分析工具：MAT、jhat

## JVM 问题分析调优经验（重点）

1、高分配速率(High Allocation Rate) 

分配速率(Allocation rate)表示单位时间内分配的内存量。通常 使用 MB/sec作为单位。上一次垃圾收集之后，与下一次GC开 始之前的年轻代使用量，两者的差值除以时间,就是分配速率。 

分配速率过高就会严重影响程序的性能，在JVM中可能会导致 巨大的GC开销。 

正常系统：分配速率较低 ~ 回收速率 -> 健康 

内存泄漏：分配速率 持续大于 回收速率 -> OOM 

性能劣化：分配速率很高 ~ 回收速率 -> 亚健康

2、过早提升(Premature Promotion) 

提升速率(promotion rate)用于衡量单位时间内从年轻代提升到老年代的数据量。一般使用 MB/sec 作为单位, 和分配速率类似。

JVM会将长时间存活的对象从年轻代提升到老年代。根据分代 假设，可能存在一种情况，老年代中不仅有存活时间长的对象，也可能有存活时间短的对象。这就是过早提升：对象存活时间还不够长的时候就被提升到了老年代。 

major GC 不是为频繁回收而设计的，但 major GC 现在也要清理这些生命短暂的对象，就会导致GC暂停时间过长。这会严重影响系统的吞吐量。

## GC 疑难情况问题分析（了解）

1、查询业务日志，可以发现这类问题：请求压力大，波峰，遭遇降级，熔断等等， 基础服务、外部API依赖 。 

2、查看系统资源和监控信息： 

硬件信息、操作系统平台、系统架构； 

排查 CPU 负载、内存不足，磁盘使用量、硬件故障、磁盘分区用满、IO 等待、IO 密集、丢数据、并发竞争等情况； 

排查网络：流量打满，响应超时，无响应，DNS 问题，网络抖动，防火墙问题，物理故障，网络参数调整、超时、连接数。 

3、查看性能指标，包括实时监控、历史数据。可以发现 假死，卡顿、响应变慢等现象； 

排查数据库， 并发连接数、慢查询、索引、磁盘空间使用量、内存使用量、网络带宽、死锁、TPS、查 询数据量、redo日志、undo、 binlog 日志、代理、工具 BUG。可以考虑的优化包括： 集群、主备、只读实例、分片、分区； 

大数据，中间件，JVM 参数。 

4、排查系统日志， 比如重启、崩溃、Kill 。 

5、APM，比如发现有些链路请求变慢等等。

6、排查应用系统 

排查配置文件: 启动参数配置、Spring 配置、JVM 监控参数、数据库参数、Log 参数、APM 配置、内存问题，比如是否存在内存泄漏，内存溢出、批处理导致的内存放大、GC 问题等等； 

GC 问题， 确定 GC 算法、确定 GC 的 KPI，GC 总耗时、GC 最大暂停时间、分析 GC 日志和监控指标：内存分配速度，分代提升速度，内存使用率等数据。适当时修改内存配置； 

排查线程, 理解线程状态、并发线程数，线程 Dump，锁资源、锁等待，死锁； 

排查代码， 比如安全漏洞、低效代码、算法优化、存储优化、架构调整、重构、解决业务代码 BUG、第三方库、XSS、CORS、正则； 

单元测试： 覆盖率、边界值、Mock 测试、集成测试。 

7、 排除资源竞争、坏邻居效应 

8、疑难问题排查分析手段 

DUMP 线程\内存； 

抽样分析\调整代码、异步化、削峰填谷。

## Java Socket 编程（重点）

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/socket.png)

## NIO 模型与相关概念（重点）

**五种IO模型**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/%E4%BA%94%E7%A7%8DIO%E6%A8%A1%E5%9E%8B.png)

**IO 模型-01**

阻塞式 IO、BIO 

一般通过在 while(true) 循环中服务端会调用 accept() 方法等待接收客户端的连接的方式监听请求，请求一旦接收到一个连接请求，就可以建立 通信套接字在这个通信套接字上进行 读写操作，此时不能再接收其他客户端连接请求，只能等待同当前连接的客户端的操作执行完成， 不过可以通过多线程来支持多个客户端的连接

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B1.png)

**IO 模型-02**

非阻塞式 IO 

和阻塞 IO 类比，内核会立即返回，返回后获得足够的 CPU 时间继续做 其它的事情。 

用户进程第一个阶段不是阻塞的,需要不断的主动询问 kernel 数据好了没有；第二个阶段依然总是阻塞的。

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B2.png)

**IO 模型-03**

IO 多路复用(IO multiplexing)，也称事件驱动 IO(event-driven IO)，就是在单个线程里同时监控多个套接字，通过select 或 poll 轮询所负责的所有socket，当某个 socket 有数据到达了， 就通知用户进程。 

IO 复用同非阻塞 IO 本质一样，不过利用了新的 select 系统调用，由内核来负责本来是请求进程该做的轮询操作。看似比非阻塞 IO 还多了一个系统调用开销， 不过因为可以支持多路 IO，才算提高了效率。 

进程先是阻塞在 select/poll 上，再是阻塞在读操作的第二个阶段上。

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B3.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B3_1.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B3_2.png)

select/poll 的几大缺点： 

（1）每次调用 select，都需要把 fd 集合从用户态拷贝到内核态，这个开销在 fd 很多时会很大 

（2）同时每次调用 select 都需要在内核遍历传递进来的所有 fd，这个开销在 fd 很多时也很大 

（3）select 支持的文件描述符数量太小了，默认是1024 

epoll（Linux 2.5.44内核中引入,2.6内核正式引入,可被用于代替 POSIX select 和 poll 系统调用）： 

（1）内核与用户空间共享一块内存 

（2）通过回调解决遍历问题 

（3）fd 没有限制，可以支撑10万连接

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8Bselect.png)

**IO 模型-04**

信号驱动 I/O 

信号驱动 IO 与 BIO 和 NIO 最大的区别就在于，在 IO 执行的数据准备阶段，不会阻塞用户进程。 

如图所示：当用户进程需要等待数据的时候，会向内核发送一个信号，告诉内核我要什么数据，然后用户进程 就继续做别的事情去了，而当内核中的数据准备好之后，内核立马发给用户进程一个信号，说”数据准备好了，快来查收“，用户进程收到信号之后，立马调用 recvfrom，去查收数据。

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B4.png)

**IO 模型-05**

异步式 IO 

异步 IO 真正实现了 IO 全流程的非阻塞。用户进程发出系统调用后立即返回，内核等待数据准备完成，然后将数据拷贝到用户进程缓冲区，然后发 送信号告诉用户进程 IO 操作执行完毕（与 SIGIO 相比，一个是发送信号告诉用户进程数据准备完毕，一个是IO执行完毕）。 

windows 的 IOCP 模型

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B5.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_02/Image/IO%E6%A8%A1%E5%9E%8B5_1.png)

一个场景，去打印店打印文件。 

- 同步阻塞 

直接排队，别的啥也干不成，直到轮到你使用打印机了，自己打印文件 

- Reactor 

拿个号码，回去该干嘛干嘛，等轮到你使用打印机了，店主通知你来用打印机，打印文件 

- Proactor 

拿个号码，回去该干嘛干嘛，等轮到你使用打印机了，店主直接给你打印好文件，通知你来拿。

## Netty 框架简介（了解）

**Netty 概览**

网络应用开发框架 

1. 异步 

2. 事件驱动 

3. 基于 NIO 

适用于: 

- 服务端 
- 客户端 
- TCP/UDP

**Netty 特性**

高性能的协议服务器: 

- 高吞吐 
- 低延迟 
- 低开销 
- 零拷贝 
- 可扩容 
- 松耦合: 网络和业务逻辑分离 
- 使用方便、可维护性好

**基本概念**

**Channel** 

通道，Java NIO 中的基础概念,代表一个打开的连接,可执行读取/写入 IO 操作。Netty 对 Channel 的所有 IO 操作都是非阻塞的。 

**ChannelFuture** 

Java 的 Future 接口，只能查询操作的完成情况, 或者阻塞当前线程等待操作完成。Netty 封装一个 ChannelFuture 接口。我们可以将回调方法传给 ChannelFuture，在操作完成时自动执行。 

**Event & Handler** 

Netty 基于事件驱动，事件和处理器可以关联到入站和出站数据流。 

入站事件： 

- 通道激活和停用 
- 读操作事件 
- 异常事件 
- 用户事件 

出站事件： 

- 打开连接 
- 关闭连接 
- 写入数据 
- 刷新数据

事件处理程序接口: 

- ChannelHandler 
- ChannelOutboundHandler 
- ChannelInboundHandler 

适配器（空实现，需要继承使用）： 

- ChannelInboundHandlerAdapter 
- ChannelOutboundHandlerAdapter

**Encoder &** **Decoder** 

处理网络 IO 时，需要进行序列化和反序列化, 转换 Java 对象与字节流。对入站数据进行解码, 基类是 ByteToMessageDecoder。对出站数据进行编码, 基类是 MessageToByteEncoder。 

**ChannelPipeline**

数据处理管道就是事件处理器链。有顺序、同一Channel的出站处理器和入站处理器在同一个列表中。

