# 学习笔记

## 到底什么是锁（重点）

**synchronized**方式的问题： 

1. 同步块的阻塞**无法中断**（不能Interruptibly） 
2. 同步块的阻塞**无法控制超时**（无法自动解锁） 
3. 同步块**无法异步处理锁**（即不能立即知道是否可以拿到锁） 
4. 同步块**无法根据条件灵活的加锁解锁**（即只能跟同步块范围一致）

更自由的锁：**Lock**

Lock接口设计： 

```java
// 1.支持中断的API 
void lockInterruptibly() throws InterruptedException;
// 2.支持超时的API 
boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
// 3.支持非阻塞获取锁的API 
boolean tryLock();
```

什么是**可重入锁**? 

-- 第二次进入时是否阻塞。 

什么是**公平锁**？ 

-- 公平锁意味着排队靠前的优先。 

-- 非公平锁则是都是同样机会。

**读写锁**

ReadWriteLock管理一组锁，一个读锁，一个写锁。读锁可以在没有写锁的时候被多个线程同时持有，写锁是独占的。所有读写锁的实现必须确保写操作对读操作的内存影响。每次只能有一个写线程，但是同时可以有多个线程并发地读数据。ReadWriteLock适用于读多写少的并发情况。

**LockSupport--锁当前线程**

LockSupport类似于Thread类的静态方法，专门处理（执行这个代码的）本线程的。

为什么 unpark 需要加一个线程作为参数？因为一个park的线程，无法自己唤醒自己，所以需要其他线程来唤醒。

**用锁的最佳实践**

Doug Lea《Java 并发编程：设计原则与模式》一书中，推荐的三个用锁的最佳实践，它们分别是： 

1. 永远只在更新对象的成员变量时加锁 

2. 永远只在访问可变的成员变量时加锁 

3. 永远不在调用其他对象的方法时加锁 

K大总结-最小使用锁： 

1. **降低锁范围**：锁定代码的范围/作用域
2. **细分锁粒度**：讲一个大锁，拆分成多个小锁

## 并发原子类（重点）

**无锁技术 – Atomic 工具类**

无锁技术的底层实现原理

- Unsafe API - **CompareAndSwap**
- CPU 硬件指令支持 - **CAS 指令**
- value的可见性 - **volatile 关键字**

核心实现原理： 

1. volatile保证读写操作都可见（注意不保证原子）；
2. 使用CAS指令，作为乐观锁实现，通过自旋重试保证写入。

CAS本质上没有使用锁。并发压力跟锁性能的关系： 

1. 压力非常小，性能本身要求就不高；
2. **压力一般的情况下，无锁更快，大部分都一次写入**；
3. 压力非常大时，自旋导致重试过多，资源消耗很大。

**LongAdder 对 AtomicLong 的改进**

通过**分段思想**改进原子类，LongAdder的改进思路： 

1. AtomicInteger和AtomicLong里的value是所有线程竞争读写的热点数据；
2. 将单个value拆分成跟线程一样多的数组Cell[]；
3. 每个线程写自己的Cell[i]++，最后对数组求和。

## 并发工具类（重点）

**什么是并发工具类**

多个线程之间怎么相互协作？

wait/notify、Lock/Condition，可以作为简单的协作机制。

更复杂的应用场景，比如

\- 我们需要控制实际并发访问资源的**并发数量**

\- 我们需要多个线程在某个时间**同时开始运行**

\- 我们需要指定数量线程**到达某个状态**再继续处理

**AQS**

AbstractQueuedSynchronizer，即抽象队列同步器。它是构建锁或者其他同步组件的基础（如Semaphore、CountDownLatch、ReentrantLock、ReentrantReadWriteLock），是JUC并发包中的核心基础组件，**抽象了竞争的资源和线程队列**。

两种资源共享方式：独占 | 共享，子类负责实现公平 OR 非公平

**Semaphore - 信号量**

1. 准入数量 N，N =1 则等价于独占锁 

2. 相当于synchronized的进化版

使用场景：**同一时间控制并发线程数**

**CountdownLatch**

**阻塞主线程，N个子线程满足条件时主线程继续**。 

使用场景：Master 线程等待 Worker 线程把任务执行完

**CyclicBarrier**

使用场景：任务执行到一定阶段, 等待其他任务对齐，**阻塞N个线程时所有线程被唤醒继续**。

**CountDownLatch与CyclicBarrier比较**

**Future/FutureTask/CompletableFuture**

**CompletableFuture**