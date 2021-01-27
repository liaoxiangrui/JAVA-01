# 学习笔记

## 什么是高性能（重点）

**性能指标**：高并发用户（Concurrent Users）、高吞吐量（Throughout）、低延迟（Latency）

延迟和响应时间（RT）的差异：延迟是系统内部的运行时间；响应时间是从用户发出请求到收到响应的时间。

**应对策略**：稳定性建设（混沌工程）：容量、爆炸半径、工程方面积累与改进

## Netty 如何实现高性能（重点）

**从Socket IO到NIO**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/SocketIO%E5%88%B0NIO.png)

**从 Socket IO 到 NIO--BIO多线程模型**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/%E4%BB%8ESocketIO%E5%88%B0NIO--BIO%E5%A4%9A%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

**从事件处理机制到 Reactor 模型**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/%E4%BB%8E%E4%BA%8B%E4%BB%B6%E5%A4%84%E7%90%86%E6%9C%BA%E5%88%B6%E5%88%B0Reactor%E6%A8%A1%E5%9E%8B.png)

Reactor 模式首先是事件驱动的，有一个或者多个并发输入源，有一个 Service Handler和多个EventHandlers。这个 Service Handler 会同步的将输入的请求多路复用的分发给相应的 Event Handler。

**从Reactor模型到 Netty NIO--01（单线程）**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Reactor%E5%8D%95%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Reactor%E5%8D%95%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B1.png)

**从Reactor模型到 Netty NIO--02（多线程）**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Reactor%E5%A4%9A%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Reactor%E5%A4%9A%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B1.png)

**从Reactor模型到 Netty NIO--03（主从）**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Reactor%E4%B8%BB%E4%BB%8E%E6%A8%A1%E5%9E%8B.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Reactor%E4%B8%BB%E4%BB%8E%E6%A8%A1%E5%9E%8B1.png)

**Netty对三种模式的支持**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Netty%E5%AF%B9%E4%B8%89%E7%A7%8D%E6%A8%A1%E5%BC%8F%E6%94%AF%E6%8C%81.png)

**Netty启动和处理流程**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Netty%E5%90%AF%E5%8A%A8%E5%92%8C%E5%A4%84%E7%90%86%E6%B5%81%E7%A8%8B.png)

**Netty线程模式**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Netty%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%BC%8F.png)

**Netty核心对象**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Netty%E6%A0%B8%E5%BF%83%E5%AF%B9%E8%B1%A1.png)

**Netty运行原理**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Netty%E8%BF%90%E8%A1%8C%E5%8E%9F%E7%90%86.png)

**关键对象**

Bootstrap: 启动线程，开启 socket 

EventLoopGroup （线程池）

EventLoop （Reactor，单线程）

SocketChannel: 连接 

ChannelInitializer: 初始化 

ChannelPipeline: 处理器链 

ChannelHandler: 处理器

## Netty 网络程序优化（了解）

**粘包与拆包**

ByteToMessageDecoder 提供的一些常见的实现类：

1. FixedLengthFrameDecoder：定长协议解码器，我们可以指定固定的字节数算一个完整的报文 
2. LineBasedFrameDecoder：行分隔符解码器，遇到\n 或者\r\n，则认为是一个完整的报文 
3. DelimiterBasedFrameDecoder：分隔符解码器，分隔符可以自己指定 
4. LengthFieldBasedFrameDecoder：长度编码解码器，将报文划分为报文头/报文体 
5. JsonObjectDecoder：json 格式解码器，当检测到匹配数量的“{”、”}”或”[””]”时，则认为是一个完整的 json 对象或者 json 数组

MTU：Maxitum Transmission Unit 最大传输单元：1500Byte

MSS：Maxitum Segment Size 最大分段大小, 为 MTU - 20(IP) - 20(TCP) ：1460Byte

**连接优化**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/%E4%B8%89%E6%AC%A1%E6%8F%A1%E6%89%8B.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/%E5%9B%9B%E6%AC%A1%E6%8C%A5%E6%89%8B.png)

Linux上MSL默认1分钟，windows上默认为2分钟。

Linux的MSL在系统参数/proc/sys/net/ipv4/tcp_fin_timeout设置，Windows的MSL在注册表TcpTimedWaitDelay中设置

**Netty 优化**

1. 不要阻塞 EventLoop
2. 系统参数优化：ulimit -a（查看文件句柄数）、/proc/sys/net/ipv4/tcp_fin_timeout、TcpTimedWaitDelay 
3. 缓冲区优化：SO_RCVBUF/SO_SNDBUF/SO_BACKLOG/ REUSEXXX 
4. 心跳周期优化：心跳机制与短线重连
5. 内存与 ByteBuffer 优化：DirectBuffer与HeapBuffer
6. 其他优化：- ioRatio 、- Watermark、- TrafficShaping

## 典型应用：API 网关（了解）

Zuul：Zuul 是 Netflix 开源的 API 网关系统，它的主要设计目标是动态路由、监控、弹性和 安全。Zuul 的内部原理可以简单看做是很多不同功能 filter 的集合，最主要的就是 pre、routing、post 这三种过滤器，分别作用于 调用业务服务 API 之前的请求处理、直接响应、调用业务服务 API 之后的响应处理。

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Zuul.png)

Zuul2：Zuul 2.x 是基于 Netty 内核重构的版本。

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Zuul2.png)

Spring Cloud Gateway

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Spring%20Cloud%20Gateway.png)

## 多线程基础（了解）

**Java 线程的创建过程**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/Java%E7%BA%BF%E7%A8%8B%E7%9A%84%E5%88%9B%E5%BB%BA%E8%BF%87%E7%A8%8B.png)

## Java 多线程（重点）

**线程状态**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_03/Image/%E7%BA%BF%E7%A8%8B%E7%8A%B6%E6%80%81.png)

**Thread 的状态改变操作**

1. Thread.sleep(long millis)，一定是当前线程调用此方法，当前线程进入 TIMED_WAITING 状态，但不释放对象锁，millis 后线程自动苏醒进入就绪状态。作用：给其它线程执行机会的最佳方式。 

2. Thread.yield()，一定是当前线程调用此方法，当前线程放弃获取的 CPU 时间片，但不释放锁资源，由运行状态变为就绪状态，让 OS 再次选择线程。作用：让相同优先级的线程轮流执行，但并不保证一定会轮流执行。实际中无法保证yield() 达到让步目的，因为让步的线程还有可能被线程调度程序再次选中。Thread.yield() 不会导致阻塞。该方法与 sleep() 类似，只是不能由用户指定暂停多长时间。 

3. t.join()/t.join(long millis)，当前线程里调用其它线程 t 的 join 方法，当前线程进入WAITING/TIMED_WAITING 状态，当前线程不会释放已经持有的对象锁，因为内部调用了t.wait，所以会释放t这个对象上的同步锁。线程t执行完毕或者millis 时间到，当前线程进入就绪状态。其中，wait操作对应的notify是由jvm底层的线程执行结束前触发的。 

4. obj.wait()，当前线程调用对象的 wait() 方法，当前线程释放obj对象锁，进入等待队列。依靠 notify()/notifyAll() 唤醒或者 wait(long timeout) timeout 时间到自动唤醒。唤醒会，线程恢复到wait时的状态。 

5. obj.notify() 唤醒在此对象监视器上等待的单个线程，选择是任意性的。notifyAll() 唤醒在此对象监视器上等待的所有线程。

**Thread 的中断与异常处理**

1. 线程内部自己处理异常，不溢出到外层（Future可以封装）。 

2. 如果线程被 Object.wait, Thread.join和Thread.sleep 三种方法之一阻塞，此时调用该线程的interrupt() 方法，那么该线程将抛出一个 InterruptedException 中断异常（该线程必须事先预备好处理此异常），从而提早地终结被阻塞状态。如果线程没有被阻塞，这时调用 interrupt() 将不起作用，直到执行到 wait/sleep/join 时,才马上会抛出InterruptedException。 

3. 如果是计算密集型的操作怎么办？ *分段处理，每个片段检查一下状态，是不是要终止*。

## 线程安全（重点）

**多线程执行会遇到什么问题?**

多个线程竞争同一资源时，如果对资源的访问顺序敏感，就称存在竞态条件。导致竞态条件发生的代码区称作临界区。不进行恰当的控制，会导致线程安全问题

**并发相关的性质**

可见性：对于可见性，Java 提供了 volatile 关键字来保证可见性。 

当一个共享变量被 volatile 修饰时，它会保证修改的值会立即被更新到主存，当有其他线程需要读取时，它会去内存中读取新值。 

另外，通过 synchronized 和 Lock 也能够保证可见性，synchronized 和 Lock 能保证同一时刻只有一个线程获取锁然后执行同步代码，并且在释放锁之前会将对变量的修改刷新到主存当中。 

**volatile 并不能保证原子性。**

有序性：Java 允许编译器和处理器对指令进行重排序，但是重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性。可以通过 volatile 关键字来保证一定的“有序性”（synchronized 和 Lock也可以）。 

happens-before 原则（先行发生原则）： 

1. 程序次序规则：一个线程内，按照代码先后顺序 

2. 锁定规则：一个 unLock 操作先行发生于后面对同一个锁的 lock 操作 

3. Volatile 变量规则：对一个变量的写操作先行发生于后面对这个变量的读操作 

4. 传递规则：如果操作 A 先行发生于操作 B，而操作 B 又先行发生于操作 C，则可以得出 A 先于 C 

5. 线程启动规则：Thread 对象的 start() 方法先行发生于此线程的每个一个动作 

6. 线程中断规则：对线程 interrupt() 方法的调用先行发生于被中断线程的代码检测到中断事件的发生 

7. 线程终结规则：线程中所有的操作都先行发生于线程的终止检测，我们可以通过 Thread.join() 方法结束、Thread.isAlive() 的返回值手段检测到线程已经终止执行 

8. 对象终结规则：一个对象的初始化完成先行发生于他的 finalize() 方法的开始

final的好处？不能被修改，通过内存屏障禁止读/写重排序，只要final引用在构造函数中没有逸出就是线程安全的。

## 线程池原理与应用（重点）

**线程池参数**

缓冲队列：

BlockingQueue 是双缓冲队列。BlockingQueue 内部使用两条队列，允许两个线程同时向队列一个存储，一个取出操作。在保证并发安全的同时，提高了队列的存取效率。 

1. ArrayBlockingQueue:规定大小的 BlockingQueue，其构造必须指定大小。其所含的对象是 FIFO 顺序排序的。 

2. LinkedBlockingQueue:大小不固定的 BlockingQueue，若其构造时指定大小，生成的 BlockingQueue 有大小限制，不指定大小，其大小有 Integer.MAX_VALUE 来决定。其所含的对象是 FIFO 顺序排序的。 

3. PriorityBlockingQueue:类似于 LinkedBlockingQueue，但是其所含对象的排序不是 FIFO，而是依据对象的自然顺序或者构造函数的 Comparator 决定。 

4. SynchronizedQueue:特殊的 BlockingQueue，对其的操作必须是放和取交替完成。

拒绝策略 ：

1. ThreadPoolExecutor.AbortPolicy: 丢弃任务并抛出 RejectedExecutionException异常。 

2. ThreadPoolExecutor.DiscardPolicy：丢弃任务，但是不抛出异常。 

3. ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新提 交被拒绝的任务 

4. ThreadPoolExecutor.CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务

**创建线程池方法**

**1. newSingleThreadExecutor** 

创建一个单线程的线程池。这个线程池只有一个线程在工作，也就是相当于单线程串行执行所有任务。如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。此线程池保证所有 任务的执行顺序按照任务的提交顺序执行。 

**2.newFixedThreadPool** 

创建固定大小的线程池。每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。 线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。 

**3. newCachedThreadPool** 

创建一个可缓存的线程池。如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲（60秒不执行任务）的线程，当任务数增加时，此线程池又可以智能的添 加新线程来处理任务。此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。 

**4.newScheduledThreadPool** 

创建一个大小无限的线程池。此线程池支持定时以及周期性执行任务的需求。

**创建固定线程池的经验**

1、如果是CPU密集型应用，则线程池大小设置为N或N+1 

2、如果是IO密集型应用，则线程池大小设置为2N或2N+2