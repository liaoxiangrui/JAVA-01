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

**基础接口 - Condition**

通过Lock.newCondition()创建。可以看做是Lock对象上的信号。类似于wait/notify，**不同之处在于Condition可以new出多个来控制线程间相互协调，并复用同一个lock**。

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

**ABA问题**：在修改数字类型的情况下其实不算问题；如果是对象引用类型修改，增加时间戳或者版本号（JDK 中 java.util.concurrent.atomic 并发包下，提供了 AtomicStampedReference，通过为引用建立个 Stamp 类似版本号的方式，确保 CAS 操作的正确性。）。

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

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_04/Image/AQS.png)

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

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_04/Image/CDL%E5%92%8CCB%E6%AF%94%E8%BE%831.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_04/Image/CDL%E5%92%8CCB%E6%AF%94%E8%BE%832.png)

**Future/FutureTask/CompletableFuture**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_04/Image/Future.png)

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_04/Image/Future1.png)

**CompletableFuture**

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_04/Image/CompletableFuture.png)

## 常用线程安全类型（重点）

**ArrayList（可调整大小的数组）**

**基本特点**：基于数组，便于按 index 访问，超过数组初始化大小需要扩容，扩容成本较高

**用途**：查询效率O(1)，增删O(n)，适合读多写少的情况，大部分情况下操作一组数据都可以用 ArrayList

**原理**：使用数组模拟列表，默认大小10，扩容 x1.5

```java
// 数组最大尺寸
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
// 扩容计算
int newCapacity = oldCapacity + (oldCapacity >> 1);
// 数组克隆浅拷贝
public Object clone() {}
```

**安全问题**：

1. 写冲突：两个写，相互操作冲突
2. 读写冲突：读，特别是 iterator 的时候，数据个数变了，拿到了非预期数据或者报错；产生ConcurrentModificationException
3. 遍历删除会有IndexOutOfBoundsException的风险（**解决方案：从后往前删、循环remove(0)**）

**LinkedList（双链表，实现了Deque接口）**

**基本特点**：使用链表实现，无需扩容

**用途**：不知道容量，插入变动多的情况

**原理**：使用双向指针将所有节点连起来

**安全问题**：

1. 写冲突：两个写，相互操作冲突
2. 读写冲突：读，特别是 iterator 的时候，数据个数变了，拿到了非预期数据或者报错；产生 ConcurrentModificationException

**List** **线程安全的简单办法**

既然线程安全是写冲突和读写冲突导致的，最简单办法就是，读写都加锁。

例如：

1. ArrayList 的方法都加上 synchronized -> Vector
2. Collections.synchronizedList，强制将 List 的操作加上同步
3. Arrays.asList，不允许添加删除，但是可以 set 替换元素
4. Collections.unmodifiableList，不允许修改内容，包括添加删除和 set

**CopyOnWriteArrayList**

**核心改进原理**：

1. 写加锁，保证不会写混乱
2. 写在一个 Copy 副本上，而不是原始数据上（GC young 区用复制，old 区用本区内的移动）

**细节**：

1. 插入元素时，在新副本操作，不影响旧引用，why？为了不影响读的效率，还可以避免读写冲突，在插入完成之前，读操作都是在旧数组。
2. 删除元素时，1）删除末尾元素，直接使用前 N-1 个元素创建一个新数组。2）删除其他位置元素，创建新数组，将剩余元素复制到新数组。
3. 读取不需要加锁，why？读取操作是直接读volatile数组索引的值，拥有可见性。增删在操作过程中都不会修改原来的数组，整个过程在新的副本里操作，增删完了替换旧数组也是在加锁的同步过程中完成的，所以读取的时候要么读旧数组，要么读替换后的新数组，在替换过程中被增删的锁同步了，因此读不需要加锁了。
4. 使用迭代器的时候，直接拿当前的数组对象做一个**快照**，此后的 List元素变动，就跟这次迭代没关系了。

**HashMap**

**基本特点**：空间换时间，哈希冲突不大的情况下查找数据性能很高

**用途**：存放指定 key 的对象，缓存对象

**原理**：使用 hash 原理，存 k-v 数据，初始容量16，扩容 x2，负载因子0.75。JDK8 以后，在链表长度到8 & 数组长度到64时，使用红黑树。

**安全问题**：

1. 写冲突
2. 读写问题，可能会死循环
3. keys()无序问题

**LinkedHashMap**

**基本特点**：继承自 HashMap，对 Entry 集合添加了一个双向链表

**用途**：保证有序，特别是 Java8 stream 操作的 toMap 时使用

**原理**：同 LinkedList，包括**插入顺序**（默认）和**访问顺序**（构造函数设置accessOrder=true）

**安全问题**：同 HashMap

**ConcurrentHashMap**

**Java7分段锁**：默认16个Segment（继承了ReentrantLock），降低锁粒度。concurrentLevel = 16，Segment[]类似分库，HashEntry[]类似分表，Segment数组的index根据哈希码高sshift位决定，HashEntry数组的index根据哈希码决定，用不同的哈希方式是为了防止数据分布不均匀。

Java 7为实现并行访问，引入了Segment 这一结构，实现了分段锁，理论上最大并发度与 Segment 个数相等。Java 8为进一步提高并发性，摒弃了分段锁的方案，而是直接使用一个大的数组，用乐观锁CAS机制。why？把之前的Segment数组和HashEntry数组合并了，在一个数组的桶上做并发控制，并且数组下面用红黑树，业务上一般都是读多写少，数据多的时候查询效率高。

## 并发编程相关内容（了解）

**线程安全操作利器** **- ThreadLocal**

- 线程本地变量
- 场景: 每个线程一个副本
- 不改方法签名静默传参（可以看做是 Context 模式，减少显式传递参数）
- 及时进行清理（防止线程污染和内存泄漏）

**并行 Stream**

多线程执行，只需要加个 parallel 即可，默认CPU核心数的线程数（终端的操作不能多线程执行，比如collect等）

**伪并发问题**

浏览器端，表单的重复提交问题

1. 客户端控制（调用方），点击后按钮不可用，跳转到其他页
2. 服务器端控制（处理端），给每个表单生成一个编号，提交时判断重复**（常用）**

## 并发编程经验总结（重点）

**加锁需要考虑的问题**

1. 粒度

2. 性能
3.  重入

4. 公平

5. 自旋锁（spinlock）

6. 场景: 脱离业务场景谈性能都是耍流氓

**线程间协作与通信**

**1.** **线程间共享**

- static/实例变量(堆内存) 
- Lock
- synchronized

**2.** **线程间协作**

- Thread#join()
- Object#wait/notify/notifyAll
- Future/Callable
- CountdownLatch
- CyclicBarrier