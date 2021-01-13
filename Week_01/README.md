# 学习笔记

## Java字节码技术（理解即可）

#### 什么是字节码？

Java bytecode 由单字节（byte）的指令组成，理论上最多支持 256 个操作码（opcode）。实际上 Java 只使用了200左右的操作码， 还有一些操作码则保留给调试操作。

根据指令的性质，主要分为四个大类：

1. 栈操作指令，包括与局部变量交互的指令
2. 程序流程控制指令
3. 对象操作指令，包括方法调用指令
4. 算术运算以及类型转换指令

疑问：new指令后面为什么还有两个字节的操作数？

解答：一个byte范围只有256，操作数可能超过256，所以就用两个数组表示，这样范围可以0~65535。

#### 生成字节码指令

```
javac [-g]
javap -c/-v
```

#### 字节码的运行时结构

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_01/Image/Frame.png)

JVM是一台基于栈的计算机器。

每个线程都有一个独属于自己的线程栈（JVM Stack），用于存储栈帧（Frame）。

每一次方法调用，JVM 都会自动创建一个栈帧。

栈帧由操作数栈，局部变量数组以及一个 Class 引用组成。

Class引用指向当前方法在运行时常量池中对应的 Class。

#### 方法调用的指令

invokestatic，顾名思义，这个指令用于调用某个类的静态方法，这是方法调用指令中最快的一个。

invokespecial, 用来调用构造函数，但也可以用于调用同一个类中的 private 方法, 以及可见的超类方法。

invokevirtual，如果是具体类型的目标对象，invokevirtual 用于调用公共，受保护和package 级的私有方法。

invokeinterface，当通过接口引用来调用方法时，将会编译为 invokeinterface 指令。

invokedynamic，JDK7 新增加的指令，是实现“动态类型语言”（Dynamically Typed Language）支持而进行的升级改进，同时也是 JDK8 以后支持 lambda 表达式的实现基础。

## JVM类加载器（重点掌握）

#### 类的生命周期

1. 加载（Loading）：找 Class 文件
2. 验证（Verification）：验证字节码格式、依赖（超类和接口）
3. 准备（Preparation）：静态字段初始化为0或null（static final修饰的固定值会初始化为真实值）、方法表记录变量使用的内存空间
4. 解析（Resolution）：符号引用（相当于索引）解析为直接引用（相当于指向实际对象）
5. 初始化（Initialization）：构造器、静态变量赋值、静态代码块
6. 使用（Using）
7. 卸载（Unloading）

#### 类加载时机

1. 当虚拟机启动时，初始化用户指定的主类，就是启动执行的 main 方法所在的类；
2. 当遇到用以新建目标类实例的 new 指令时，初始化 new 指令的目标类，就是 new一个类的时候要初始化；
3. 当遇到调用静态方法的指令时，初始化该静态方法所在的类；
4. 当遇到访问静态字段的指令时，初始化该静态字段所在的类；
5. 子类的初始化会触发父类的初始化；
6. 如果一个接口定义了 default 方法，那么直接实现或者间接实现该接口的类的初始化，会触发该接口的初始化；
7. 使用反射 API 对某个类进行反射调用时，初始化这个类，其实跟前面一样，反射调用要么是已经有实例了，要么是静态方法，都需要初始化；
8. 当初次调用 MethodHandle 实例时，初始化该 MethodHandle 指向的方法所在的类。

#### 不会初始化（可能会加载）

1. 通过子类引用父类的静态字段，只会触发父类的初始化，而不会触发子类的初始化。
2. 定义对象数组，不会触发该类的初始化。
3. 常量在编译期间会存入调用类的常量池中，本质上并没有直接引用定义常量的类，不会触发定义常量所在的类。
4. 通过类名获取 Class 对象，不会触发类的初始化，Hello.class 不会让 Hello 类初始化。
5. 通过 Class.forName 加载指定类时，如果指定参数 initialize 为 false 时，也不会触发类初始化，其实这个参数是告诉虚拟机，是否要对类进行初始化。Class.forName（“jvm.Hello”）默认会加载初始化Hello类。
6. 通过 ClassLoader 默认的 loadClass 方法，也不会触发初始化动作（加载了，但是不初始化）。

#### 三类加载器

1. 启动类加载器（BootstrapClassLoader）

2. 扩展类加载器（ExtClassLoader）

3. 应用类加载器（AppClassLoader）

加载器特点：

1. 双亲委托
2. 负责依赖
3. 缓存加载

#### 添加引用类的几种方式

1. 放到 JDK 的 lib/ext 下，或者-Djava.ext.dirs
2. java –cp/classpath 或者 class 文件放到当前路径
3. 自定义 ClassLoader 加载
4. 拿到当前执行类的 ClassLoader，反射调用 addUrl 方法添加 Jar 或路径(JDK9 无效)。

## JVM内存模型（重点掌握）

#### JVM内存结构

每个线程都只能访问自己的线程栈。

每个线程都不能访问（看不见）其他线程的局部变量。

所有原生类型的局部变量都存储在线程栈中，因此对其他线程是不可见的。

线程可以将一个原生变量值的副本传给另一个线程，但不能共享原生局部变量本身。

堆内存中包含了 Java 代码中创建的所有对象，不管是哪个线程创建的。 其中也涵盖了包装类型（例如 Byte，Integer，Long 等）。

不管是创建一个对象并将其赋值给局部变量， 还是赋值给另一个对象的成员变量， 创建的对象都会被保存到堆内存中。

如果是原生数据类型的局部变量，那么它的内容就全部保留在线程栈上。

如果是对象引用，则栈中的局部变量槽位中保存着对象的引用地址，而实际的对象内容保存在堆中。

对象的成员变量与对象本身一起存储在堆上, 不管成员变量的类型是原生数值，还是对象引用。

类的静态变量则和类定义一样都保存在堆中。

总结：方法中使用的原生数据类型和对象引用地址在栈上存储；对象、对象成员与类定义、静态变量在堆上。

堆内存又称为“共享堆”，堆中的所有对象，可以被所有线程访问, 只要他们能拿到对象的引用地址。

如果一个线程可以访问某个对象时，也就可以访问该对象的成员变量。

如果两个线程同时调用某个对象的同一方法，则它们都可以访问到这个对象的成员变量，但每个线程的局部变量副本是独立的。

#### JVM内存整体结构

每启动一个线程，JVM 就会在栈空间栈分配对应的 线程栈, 比如 1MB 的空间（-Xss1m）。

线程栈也叫做 Java 方法栈。 如果使用了JNI 方法，则会分配一个单独的本地方法栈(Native Stack)。

线程执行过程中，一般会有多个方法组成调用栈（Stack Trace）, 比如 A 调用 B，B 调用 C。。。每执行到一个方法，就会创建对应的 栈帧（Frame）。

#### JVM栈内存结构

栈帧是一个逻辑上的概念，具体的大小在一个方法编写完成后基本上就能确定。

比如返回值 需要有一个空间存放吧，每个局部变量都需要对应的地址空间，此外还有给指令使用的 操作数栈，以及 class 指针（标识这个栈帧对应的是哪个类的方法, 指向非堆里面的 Class 对象）。

#### JVM堆内存结构

堆内存是所有线程共用的内存空间，JVM 将Heap 内存分为年轻代（Young generation）和 老年代（Old generation, 也叫 Tenured）两部分。

年轻代还划分为 3 个内存池，新生代（Eden space）和存活区（Survivor space）, 在大部分GC 算法中有 2 个存活区（S0, S1），在我们可以观察到的任何时刻，S0 和 S1 总有一个是空的, 但一般较小，也不浪费多少空间。

Non-Heap 本质上还是 Heap，只是一般不归 GC管理，里面划分为 3 个内存池。

Metaspace, 以前叫持久代（永久代, Permanent generation）, Java8 换了个名字叫 Metaspace. CCS, Compressed Class Space, 存放 class 信息的，和 Metaspace 有交叉。

Code Cache, 存放 JIT 编译器编译后的本地机器代码。

年轻代：存放新对象或生命周期短的对象。

老年代：存放大对象或生命周期长的对象。

#### CPU与内存行为

CPU 乱序执行

volatile 关键字

原子性操作

内存屏障

#### 什么是JMM？

JMM 规范对应的是“[JSR-133. Java Memory Model and Thread Specification]”，《Java 语言规范》的 [$17.4. Memory Model章节]

JMM 规范明确定义了不同的线程之间，通过哪些方式，在什么时候可以看见其他线程保存到共享变量中的值；以及在必要时，如何对共享变量的访问进行同步。这样的好处是屏蔽各种硬件平台和操作系统之间的内存访问差异，实现了 Java 并发程序真正的跨平台。

所有的对象(包括内部的实例成员变量)，static 变量，以及数组，都必须存放到堆内存中。

局部变量，方法的形参/入参，异常处理语句的入参不允许在线程之间共享，所以不受内存模型的影响。

多个线程同时对一个变量访问时【读取/写入】，这时候只要有某个线程执行的是写操作，那么这种现象就称之为“冲突”。

可以被其他线程影响或感知的操作，称为线程间的交互行为， 可分为： 读取、写入、同步操作、外部操作等等。 其中同步操作包括：对 volatile 变量的读写，对管程（monitor）的锁定与解锁，线程的起始操作与结尾操作，线程启动和结束等等。 外部操作则是指对线程执行环境之外的操作，比如停止其他线程等等。

JMM 规范的是线程间的交互操作，而不管线程内部对局部变量进行的操作。

## JVM启动参数（了解即可）

#### 启动参数

以-开头为标准参数，所有的 JVM 都要实现这些参数，并且向后兼容。

-D 设置系统属性。

以 -X 开头为非标准参数， 基本都是传给 JVM 的，默认 JVM 实现这些参数的功能，但是并不保证所有 JVM 实现都满足，且不保证向后兼容。 可以使用 java -X 命令来查看当前 JVM 支持的非标准参数。

以–XX:开头为非稳定参数, 专门用于控制 JVM的行为，跟具体的 JVM 实现有关，随时可能会在下个版本取消。

-XX:+-Flags 形式, +- 是对布尔值进行开关。

-XX:key=value 形式, 指定某个选项的值。

-server

-Dfile.encoding=UTF-8 

-Xmx8g

-XX:+UseG1GC

-XX:MaxPermSize=256m

#### 系统属性

-Dfile.encoding=UTF-8

-Duser.timezone=GMT+08

-Dmaven.test.skip=true

-Dio.netty.eventLoopThreads=8

#### 运行模式

1. -server：设置 JVM 使用 server 模式，特点是启动速度比较慢，但运行时性能和内存管理效率很高，适用于生产环境。在具有 64 位能力的 JDK 环境下将默认启用该模式，而忽略 -client 参数。

2. -client ：JDK1.7 之前在32位的 x86 机器上的默认值是 -client 选项。设置 JVM 使用 client 模式，特点是启动速度比较快，但运行时性能和内存管理效率不高，通常用于客户端应用程序或者 PC 应用开发和调试。此外，我们知道 JVM 加载字节码后，可以解释执行，也可以编译成本地代码再执行，所以可以配置 JVM 对字节码的处理模式：

3. -Xint：在解释模式（interpreted mode）下运行，-Xint 标记会强制 JVM 解释执行所有的字节码，这当然会降低运行速度，通常低10倍或更多。

4. -Xcomp：-Xcomp 参数与-Xint 正好相反，JVM 在第一次使用时会把所有的字节码编译成本地代码，从而带来最大程度的优化。【注意预热】

5. -Xmixed：-Xmixed 是混合模式，将解释模式和编译模式进行混合使用，有 JVM 自己决定，这是 JVM 的默认模式，也是推荐模式。 我们使用 java -version 可以看到 mixed mode 等信息。

#### 堆内存

-Xmx, 指定最大堆内存。 如 -Xmx4g. 这只是限制了 Heap 部分的最大值为4g。这个内存不包括栈内存，也不包括堆外使用的内存。

-Xms, 指定堆内存空间的初始大小。 如 -Xms4g。 而且指定的内存大小，并不是操作系统实际分配的初始值，而是GC先规划好，用到才分配。 专用服务器上需要保持 –Xms 和 –Xmx 一致，否则应用刚启动可能就有好几个 FullGC。当两者配置不一致时，堆内存扩容可能会导致性能抖动。

-Xmn, 等价于 -XX:NewSize，使用 G1 垃圾收集器 不应该 设置该选项，在其他的某些业务场景下可以设置。官方建议设置为 -Xmx 的 1/2 ~ 1/4.

-XX:MaxPermSize=size, 这是 JDK1.7 之前使用的。Java8 默认允许的Meta空间无限大，此参数无效。

-XX:MaxMetaspaceSize=size, Java8 默认不限制 Meta 空间, 一般不允许设置该选项。

-XX:MaxDirectMemorySize=size，系统可以使用的最大堆外内存，这个参数跟 -Dsun.nio.MaxDirectMemorySize 效果相同。

-Xss, 设置每个线程栈的字节数。 例如 -Xss1m 指定线程栈为 1MB，与-XX:ThreadStackSize=1m 等价

#### GC相关

-XX:+UseG1GC：使用 G1 垃圾回收器

-XX:+UseConcMarkSweepGC：使用 CMS 垃圾回收器

-XX:+UseSerialGC：使用串行垃圾回收器

-XX:+UseParallelGC：使用并行垃圾回收器

// Java 11+

-XX:+UnlockExperimentalVMOptions -XX:+UseZGC 

// Java 12+

-XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC

#### 分析诊断

-XX:+-HeapDumpOnOutOfMemoryError 选项, 当 OutOfMemoryError 产生，即内存溢出(堆内存或持久代)时，自动 Dump 堆内存。

示例用法： java -XX:+HeapDumpOnOutOfMemoryError -Xmx256m ConsumeHeap

-XX:HeapDumpPath 选项, 与 HeapDumpOnOutOfMemoryError 搭配使用, 指定内存溢出时 Dump 文件的目录。

如果没有指定则默认为启动 Java 程序的工作目录。

示例用法： java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/local/ ConsumeHeap 自动 Dump 的 hprof 文件会存储到 /usr/local/ 目录下。

-XX:OnError 选项, 发生致命错误时（fatal error）执行的脚本。

例如, 写一个脚本来记录出错时间, 执行一些命令, 或者 curl 一下某个在线报警的 url. 

示例用法：java -XX:OnError="gdb - %p" MyApp 

可以发现有一个 %p 的格式化字符串，表示进程 PID。 

-XX:OnOutOfMemoryError 选项, 抛出 OutOfMemoryError 错误时执行的脚本。

-XX:ErrorFile=filename 选项, 致命错误的日志文件名,绝对路径或者相对路径。

-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1506，远程调试

#### JavaAgent

Agent 是 JVM 中的一项黑科技, 可以通过无侵入方式来做很多事情，比如注入 AOP 代码，执行统计等等，权限非常大。这里简单介绍一下配置选项，详细功能需要专门来讲。

设置 agent 的语法如下: 

-agentlib:libname[=options] 启用 native 方式的 agent, 参考 LD_LIBRARY_PATH 路径。

-agentpath:pathname[=options] 启用 native 方式的 agent。 

-javaagent:jarpath[=options] 启用外部的 agent 库, 比如 pinpoint.jar 等等。

-Xnoagent 则是禁用所有 agent。

以下示例开启 CPU 使用时间抽样分析:

JAVA_OPTS="-agentlib:hprof=cpu=samples,file=cpu.samples.log"

## JVM命令行工具（重点掌握）

#### jps/jinfo

jinfo pid

#### jstat

\> jstat -options

-class 类加载(Class loader)信息统计. 

-compiler JIT 即时编译器相关的统计信息。

-gc GC 相关的堆内存信息. 用法: jstat -gc -h 10 -t 864 1s 20

-gccapacity 各个内存池分代空间的容量。

-gccause 看上次 GC, 本次 GC（如果正在 GC中）的原因, 其他输出和 -gcutil 选项一致。

-gcnew 年轻代的统计信息. （New = Young = Eden + S0 + S1） 

-gcnewcapacity 年轻代空间大小统计. 

-gcold 老年代和元数据区的行为统计。

-gcoldcapacity old 空间大小统计. 

-gcmetacapacity meta 区大小统计. 

-gcutil GC 相关区域的使用率（utilization）统计。

-printcompilation 打印 JVM 编译统计信息。

演示：

jstat -gcutil pid 1000 1000

#### jmap

常用选项就 3 个：

-heap 打印堆内存（/内存池）的配置和使用信息。

-histo 看哪些类占用的空间最多, 直方图

-dump:format=b,file=xxxx.hprof Dump 堆内存。

演示:

jmap -heap pid

jmap -histo pid

jmap -dump:format=b,file=3826.hprof 3826

#### jstack

-F 强制执行 thread dump. 可在 Java 进程卡死（hung 住）时使用, 此选项可能需要系统权限。

-m 混合模式(mixed mode),将 Java 帧和 native帧一起输出, 此选项可能需要系统权限。

-l 长列表模式. 将线程相关的 locks 信息一起输出，比如持有的锁，等待的锁。

演示：

jstack -l pid

#### jcmd

示例：

jcmd pid help

jcmd pid VM.version

jcmd pid VM.flags

jcmd pid VM.command_line

jcmd pid VM.system_properties

jcmd pid Thread.print

jcmd pid GC.class_histogram

jcmd pid GC.heap_info

#### jrunscript/jjs

当curl命令用：

jrunscript -e "cat('http://www.baidu.com')"

执行js脚本片段

jrunscript -e "print('hello,kk.jvm'+1)"

执行js文件

jrunscript -l js -f /XXX/XXX/test.js

## JVM图形化工具（重点掌握）

#### jconsole

在命令行输入 jconsole 即可打开

本地 JVM 可以直接选择

远程 JVM 可以通过 JMX 方式连接

共有 6 个面板

第一个为概览，四项指标具体为：

堆内存使用量：此处展示的就是前面 Java 内存模型课程中提到的堆内存使用情况，从图上可以看到，堆内存使用了 94MB 左右，并且一 

直在增长。

线程：展示了 JVM 中活动线程的数量，当前时刻共有 17 个活动线程。

类：JVM 一共加载了 5563 个类，没有卸载类。

CPU 占用率：目前 CPU 使用率为 0.2%，这个数值非常低，且最高的时候也不到 3%，初步判断系统当前并没有什么负载和压力。

有如下几个时间维度可供选择：

1分钟、5分钟、10分钟、30分钟、1小时、2小时、3小时、6小时、12小时、1天、7天、1个月、3个月、6个月、1年、全部，一共是16

档。

当我们想关注最近1小时或者1分钟的数据，就可以选择对应的档。旁边的3个标签页(内存、线程、类)，也都支持选择时间范围。

内存图表包括：

• 堆内存使用量，主要包括老年代（内存池 “PS Old Gen”）、新生代（“PS Eden Space”）、存活区（“PS Survivor Space”）；

• 非堆内存使用量，主要包括内存池“Metaspace”、 “Code Cache”、“Compressed Class Space”等；

可以分别选择对应的 6 个内存池。

通过内存面板，我们可以看到各个区域的内存使用和变化情况，并且可以：

1. 手动执行gc，见图上的标号1，点击按钮即可执行JDK中的 System.gc()
2. 通过图中右下角标号 2 的界面，可以看到各个内存池的百分比使用率，以及堆/非堆空间的汇总使用 情况
3. 从左下角标号 3 的界面，可以看到 JVM 使用的垃圾收集器，以及执行垃圾收集的次数，以及相应的时间消耗。

线程面板展示了线程数变化信息，以及监测到的线程列表。

我们可以常根据名称直接查看线程的状态（运行还是等待中）和调用栈（正在执行什么操作）。

特别地，我们还可以直接点击“检测死锁”按钮来检测死锁，如果没有死锁则会提示“未检测到死锁”。

类监控面板，可以直接看到 JVM 加载和卸载的类数量汇总信息，以及随着时间的动态变化。

VM 概要的数据有五个部分：

第一部分是虚拟机的信息；

第二部分是线程数量，以及类加载的汇总信息；

第三部分是堆内存和 GC 统计。

第四部分是操作系统和宿主机的设备信息，比如 CPU 数量、物理内存、虚拟内存等等。

第五部分是 JVM 启动参数和几个关键路径，这些信息其实跟 jinfo 命令看到的差不多。

#### jvisualvm

特点：抽样器（一定时间内系统CPU和内存的变化情况）

#### visualgc

#### jmc

飞行纪录

## GC的背景和一般原理（了解）

标记清除算法（Mark and Sweep） 

• Marking（标记）: 遍历所有的可达对象，并在本地内存(native)中分门别类记下。

• Sweeping（清除）: 这一步保证了，不可达对象所占用的内存，在之后进行内存分配时可以重用。

并行 GC 和 CMS 的基本原理。

优势：可以处理循环依赖，只扫描部分对象

除了清除，还要做压缩。

怎么才能标记和清除清楚上百万对象呢？

答案就是 STW，让全世界停止下来。

对象分配在新生代的 Eden 区，标记阶段 Eden 区存活的对象就会复制到存活区；

由如下参数控制提升阈值（younggc15次存活的对象会去老年代）

默认-XX:+MaxTenuringThreshold=15

老年代默认都是存活对象，采用移动方式：

1. 标记所有通过 GC roots 可达的对象；

2. 删除所有不可达对象；

3. 整理老年代空间中的内容，方法是将所有的存活对象复制，从老年代空间开始的地方依次存放。

可以作为 GC Roots 的对象

1. 当前正在执行的方法里的局部变量和输入参数

2. 活动线程（Active threads）

3. 所有类的静态字段（static field）

4. JNI 引用

此阶段暂停的时间，与堆内存大小,对象的总数没有直接关系，而是由存活对象（alive objects）的数量来决定。所以增加堆内存的大小并不会直接影响标记阶段占用的时间。

## 串行GC/并行GC（SerialGC/ParallelGC）（重点掌握）

#### 串行GC（SerialGC）/ParNewGC

-XX:+UseSerialGC 配置串行 GC

串行 GC 对年轻代使用 mark-copy（标记-复制） 算法，对老年代使用 mark-sweep-compact（标记-清除-整理）算法。

两者都是单线程的垃圾收集器，不能进行并行处理，所以都会触发全线暂停（STW），停止所有的应用线程。

因此这种 GC 算法不能充分利用多核 CPU。不管有多少 CPU 内核，JVM 在垃圾收集时都只能使用单个核心。

CPU 利用率高，暂停时间长。简单粗暴，就像老式的电脑，动不动就卡死。

该选项只适合几百 MB 堆内存的 JVM，而且是单核 CPU 时比较有用。

-XX:+USeParNewGC 改进版本的 Serial GC，可以配合 CMS 使用。

#### 并行GC（ParallelGC）

-XX:+UseParallelGC

-XX:+UseParallelOldGC

-XX:+UseParallelGC -XX:+UseParallelOldGC

年轻代和老年代的垃圾回收都会触发 STW 事件。

在年轻代使用 标记-复制（mark-copy）算法，在老年代使用 标记-清除-整理（mark-sweep-compact）算法。 

-XX:ParallelGCThreads=N 来指定 GC 线程数， 其默认值为 CPU 核心数（cpu<=8?cpu:5*cpu/8+3；8core以下是CPU核心数，超过8core比较多的话，只用一大半。）。

并行垃圾收集器适用于多核服务器，主要目标是增加吞吐量。因为对系统资源的有效使用，能达到更高的吞吐量: 

• 在 GC 期间，所有 CPU 内核都在并行清理垃圾，所以总暂停时间更短；

• 在两次 GC 周期的间隔期，没有 GC 线程在运行，不会消耗任何系统资源。

## CMS GC/G1 GC（重点掌握）

#### CMS GC（Mostly Concurrent Mark and Sweep Garbage Collector）

-XX:+UseConcMarkSweepGC

其对年轻代采用并行 STW 方式的 mark-copy (标记-复制)算法，对老年代主要使用并发 mark-sweep (标记-清除)算法。

CMS GC 的设计目标是避免在老年代垃圾收集时出现长时间的卡顿，主要通过两种手段来达成此目标：

1. 不对老年代进行整理，而是使用空闲列表（free-lists）来管理内存空间的回收。

2. 在 mark-and-sweep （标记-清除） 阶段的大部分工作和应用线程一起并发执行。

也就是说，在这些阶段并没有明显的应用线程暂停。但值得注意的是，它仍然和应用线程争抢CPU 时间。默认情况下，CMS 使用的并发线程数等于 CPU 核心数的 1/4（ConcGCThreads = (ParallelGCThreads + 3)/4 向下去整）。

如果服务器是多核 CPU，并且主要调优目标是降低 GC 停顿导致的系统延迟，那么使用 CMS 是个很明智的选择。进行老年代的并发回收时，可能会伴随着多次年轻代的 minor GC。

思考：并行 Parallel 与并发 Concurrent 的区别？（并行：应用线程停下来，所有CPU资源给GC线程使用；并发：应用线程和GC线程共同竞争CPU资源）

Parallel GC和CMS GC的最大young区大小如何计算：

Parallel GC：Xmx的1/3；CMS GC：64M * 并发GC线程数 * 13 / 10和Xmx的1/3取小的那个。

#### 六个阶段

阶段 1: Initial Mark（初始标记）

这个阶段伴随着 STW 暂停。初始标记的目标是标记所有的根对象，包括根对象直接引用的对象，以及被年轻代中所有存活对象所引用的对象（老年代单独回收）。

阶段 2: Concurrent Mark（并发标记）

在此阶段，CMS GC 遍历老年代，标记所有的存活对象，从前一阶段 “Initial Mark” 找到的根对象开始算起。 “并发标记”阶段，就是与应用程序同时运行，不用暂停的阶段。

阶段 3: Concurrent Preclean（并发预清理）

此阶段同样是与应用线程并发执行的，不需要停止应用线程。 因为前一阶段【并发标记】与程序并发运行，可能有一些引用关系已经发生了改变。如果在并发标记过程中引用关系发生了变化，JVM 会通过“Card（卡片）”的方式将发生了改变的区域标记为“脏”区，这就是所谓的卡片标记（Card Marking）。

阶段 4: Final Remark（最终标记）

最终标记阶段是此次 GC 事件中的第二次（也是最后一次）STW 停顿。本阶段的目标是完成老年代中所有存活对象的标记. 因为之前的预清理阶段是并发执行的，有可能 GC 线程跟不上应用程序的修改速度。所以需要一次STW 暂停来处理各种复杂的情况。通常 CMS 会尝试在年轻代尽可能空的情况下执行 Final Remark 阶段，以免连续触发多次 STW 事件。

阶段 5: Concurrent Sweep（并发清除）

此阶段与应用程序并发执行，不需要 STW 停顿。JVM 在此阶段删除不再使用的对象，并回收他们占用的内存空间。

阶段 6: Concurrent Reset（并发重置）

此阶段与应用程序并发执行，重置 CMS 算法相关的内部数据，为下一次 GC 循环做准备。

总结：CMS 垃圾收集器在减少停顿时间上做了很多复杂而有用的工作，用于垃圾回收的并发线程执行的同时，并不需要暂停应用线程。 当然，CMS 也有一些缺点，其中最大的问题就是老年代内存碎片问题（因为不压缩），在某些情况下 GC 会造成不可预测的暂停时间，特别是堆内存较大的情况下（因为堆内存较大，最终标记阶段的对象可能越多越复杂）。

#### G1 GC

G1 的全称是 Garbage-First，意为垃圾优先，哪一块的垃圾最多就优先清理它。

G1 GC 最主要的设计目标是：将 STW 停顿的时间和分布，变成可预期且可配置的。

事实上，G1 GC 是一款软实时垃圾收集器，可以为其设置某项特定的性能指标。为了达成可预期停顿时间的指标，G1 GC 有一些独特的实现。

首先，堆不再分成年轻代和老年代，而是划分为多个（通常是 2048 个）可以存放对象的 小块堆区域(smaller heap regions)。每个小块，可能一会被定义成 Eden 区，一会被指定为 Survivor区或者Old 区。在逻辑上，所有的 Eden 区和 Survivor区合起来就是年轻代，所有的 Old 区拼在一起那就是老年代

-XX:+UseG1GC -XX:MaxGCPauseMillis=50（默认200）

这样划分之后，使得 G1 不必每次都去收集整个堆空间，而是以增量的方式来进行处理: 每次只处理一部分内存块，称为此次 GC 的回收集(collection set)。每次 GC 暂停都会收集所有年轻代的内存块，但一般只包含部分老年代的内存块。

G1 的另一项创新是，在并发阶段估算每个小堆块存活对象的总数。构建回收集的原则是：垃圾最多的小块会被优先收集。这也是 G1 名称的由来。

#### 配置参数

-XX:+UseG1GC：启用 G1 GC； 

-XX:G1NewSizePercent：初始年轻代占整个 Java Heap 的大小，默认值为 5%； 

-XX:G1MaxNewSizePercent：最大年轻代占整个 Java Heap 的大小，默认值为 60%； 

-XX:G1HeapRegionSize：设置每个 Region 的大小，单位 MB，需要为 1，2，4，8，16，32 中的某个值，默认是堆内存的 1/2000。如果这个值设置比较大，那么大对象就可以进入 Region 了。

-XX:ConcGCThreads：与 Java 应用一起执行的 GC 线程数量，默认是 Java 线程的 1/4，减少这个参数的数值可 能会提升并行回收的效率，提高系统内部吞吐量。如果这个数值过低，参与回收垃圾的线程不足，也会导致并行回收机制耗时加长。

-XX:+InitiatingHeapOccupancyPercent（简称 IHOP）：G1 内部并行回收循环启动的阈值，默认为 Java Heap 的 45%。这个可以理解为老年代使用大于等于 45% 的时候，JVM 会启动垃圾回收。这个值非常重要，它决定了在 什么时间启动老年代的并行回收。

-XX:G1HeapWastePercent：G1停止回收的最小内存大小，默认是堆大小的 5%。GC 会收集所有的 Region 中 的对象，但是如果下降到了 5%，就会停下来不再收集了。就是说，不必每次回收就把所有的垃圾都处理完，可以 遗留少量的下次处理，这样也降低了单次消耗的时间。

-XX:G1MixedGCCountTarget：设置并行循环之后需要有多少个混合 GC 启动，默认值是 8 个。老年代 Regions 的回收时间通常比年轻代的收集时间要长一些。所以如果混合收集器比较多，可以允许 G1 延长老年代的收集时间。

-XX:+G1PrintRegionLivenessInfo：这个参数需要和 -XX:+UnlockDiagnosticVMOptions 配合启动，打印 JVM 的调试信息，每个 Region 里的对象存活信息。

-XX:G1ReservePercent：G1 为了保留一些空间用于年代之间的提升，默认值是堆空间的 10%。因为大量执行回收的地方在年轻代（存活时间较短），所以如果你的应用里面有比较大的堆内存空间、比较多的大对象存活，这里需要保留一些内存。

-XX:+G1SummarizeRSetStats：这也是一个 VM 的调试信息。如果启用，会在 VM 退出的时候打印出 Rsets 的详细总结信息。如果启用 -XX:G1SummaryRSetStatsPeriod 参数，就会阶段性地打印 Rsets 信息。

-XX:+G1TraceConcRefinement：这个也是一个 VM 的调试信息，如果启用，并行回收阶段的日志就会被详细打印出来。

-XX:+GCTimeRatio：这个参数就是计算花在 Java 应用线程上和花在 GC 线程上的时间比率，默认是 9，跟新生代内存的分配比例一致。这个参数主要的目的是让用户可以控制花在应用上的时间，G1 的计算公式是 100/（1+GCTimeRatio）。这样如果参数设置为 9，则最多 10% 的时间会花在 GC 工作上面。Parallel GC 的默认值是 99，表示 1% 的时间被用在 GC 上面，这是因为 Parallel GC 贯穿整个 GC，而 G1 则根据 Region 来进行划分，不需要全局性扫描整个内存堆。

-XX:+UseStringDeduplication：手动开启 Java String 对象的去重工作，这个是 JDK8u20 版本之后新增的参数，主要用于相同 String 避免重复申请内存，节约 Region 的使用。

-XX:MaxGCPauseMillis：预期 G1 每次执行 GC 操作的暂停时间，单位是毫秒，默认值是 200 毫秒，G1 会尽量保证控制在这个范围内。

#### 步骤

1、年轻代模式转移暂停（Evacuation Pause）

G1 GC 会通过前面一段时间的运行情况来不断的调整自己的回收策略和行为，以此来比较稳定地控制暂停时间。在应用程序刚启动时，G1 还没有采集到什么足够的信息，这时候就处于初始的 fully-young 模式。当年轻代空间用满后，应用线程会被暂停，年轻代内存块中的存活对象被拷贝到存活区。如果还没有存活区，则任意选择一部分空闲的内存块作为存活区。

拷贝的过程称为转移（Evacuation)，这和前面介绍的其他年轻代收集器是一样的工作原理。

2、并发标记（Concurrent Marking）

同时我们也可以看到，G1 GC 的很多概念建立在 CMS 的基础上，所以下面的内容需要对 CMS 有一定的理解。

G1 并发标记的过程与 CMS 基本上是一样的。G1 的并发标记通过 Snapshot-At-The-Beginning（起始快照）的方式，在标记阶段开始时记下所有的存活对象。即使在标记的同时又有一些变成了垃圾。通过对象的存活信息，可以构建出每个小堆块的存活状态，以便回收集能高效地进行选择。

这些信息在接下来的阶段会用来执行老年代区域的垃圾收集。

有两种情况是可以完全并发执行的：

一、如果在标记阶段确定某个小堆块中没有存活对象，只包含垃圾；

二、在 STW 转移暂停期间，同时包含垃圾和存活对象的老年代小堆块。

当堆内存的总体使用比例达到一定数值，就会触发并发标记。这个默认比例是 45%，但也可以通过 JVM参数 InitiatingHeapOccupancyPercent 来设置。和 CMS 一样，G1 的并发标记也是由多个阶段组成，其中一些阶段是完全并发的，还有一些阶段则会暂停应用线程。

阶段 1: Initial Mark（初始标记）

此阶段标记所有从 GC 根对象直接可达的对象。

阶段 2: Root Region Scan（Root区扫描）

此阶段标记所有从 "根区域" 可达的存活对象。根区域包括：非空的区域，以及在标记过程中不得不收集的区域。

阶段 3: Concurrent Mark（并发标记）

此阶段和 CMS 的并发标记阶段非常类似：只遍历对象图，并在一个特殊的位图中标记能访问到的对象。

阶段 4: Remark（再次标记） 

和 CMS 类似，这是一次 STW 停顿(因为不是并发的阶段)，以完成标记过程。 G1 收集器会短暂地停止应用线程，停止并发更新信息的写入，处理其中的少量信息，并标记所有在并发标记开始时未被标记的存活对象。

阶段 5: Cleanup（清理）

最后这个清理阶段为即将到来的转移阶段做准备，统计小堆块中所有存活的对象，并将小堆块进行排序，以提升GC 的效率，维护并发标记的内部状态。 所有不包含存活对象的小堆块在此阶段都被回收了。有一部分任务是并发的：例如空堆区的回收，还有大部分的存活率计算。此阶段也需要一个短暂的 STW 暂停。

3、转移暂停: 混合模式（Evacuation Pause (mixed)）

并发标记完成之后，G1将执行一次混合收集（mixed collection），就是不只清理年轻代，还将一部分老年代区域也加入到 回收集 中。混合模式的转移暂停不一定紧跟并发标记阶段。有很多规则和历史数据会影响混合模式的启动时机。比如，假若在老年代中可以并发地腾出很多的小堆块，就没有必要启动混合模式。

因此，在并发标记与混合转移暂停之间，很可能会存在多次 young 模式的转移暂停。

具体添加到回收集的老年代小堆块的大小及顺序，也是基于许多规则来判定的。其中包括指定的软实时性能指标，存活性，以及在并发标记期间收集的 GC 效率等数据，外加一些可配置的 JVM 选项。混合收集的过程，很大程度上和前面的 fully-young gc 是一样的。

#### 注意事项

特别需要注意的是，某些情况下 G1 触发了 Full GC，这时 G1 会退化使用 Serial 收集器来完成垃圾的清理工作， 它仅仅使用单线程来完成 GC 工作，GC 暂停时间将达到秒级别的。

1.并发模式失败

G1 启动标记周期，但在 Mix GC 之前，老年代就被填满，这时候 G1 会放弃标记周期。解决办法：增加堆大小， 或者调整周期（例如增加线程数-XX:ConcGCThreads 等）。

2.晋升失败

没有足够的内存供存活对象或晋升对象使用，由此触发了 Full GC(to-space exhausted/to-space overflow）。

**解决办法：**

a) 增加 –XX:G1ReservePercent 选项的值（并相应增加总的堆大小）增加预留内存量。

b) 通过减少 –XX:InitiatingHeapOccupancyPercent 提前启动标记周期。

c) 也可以通过增加 –XX:ConcGCThreads 选项的值来增加并行标记线程的数目。

3.巨型对象分配失败

当巨型对象找不到合适的空间进行分配时，就会启动 Full GC，来释放空间。

解决办法：增加内存或者增大 -XX:G1HeapRegionSize

#### 常用的GC组合

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_01/Image/%E5%B8%B8%E7%94%A8GC%E7%BB%84%E5%90%88.png)

常用的组合为：

（1）Serial+Serial Old 实现单线程的低延迟垃圾回收机制；

（2）ParNew+CMS，实现多线程的低延迟垃圾回收机制；

（3）Parallel Scavenge和Parallel Scavenge Old，实现多线程的高吞吐量垃圾回收机制；

#### GC如何选择

![](https://github.com/liaoxiangrui/JAVA-01/blob/main/Week_01/Image/GC%E5%AF%B9%E6%AF%94.png)

选择正确的 GC 算法，唯一可行的方式就是去尝试，一般性的指导原则：

1. 如果系统考虑吞吐优先，CPU 资源都用来最大程度处理业务，用 Parallel GC；

2. 如果系统考虑低延迟有限，每次 GC 时间尽量短，用 CMS GC；

3. 如果系统内存堆较大，同时希望整体来看平均 GC 时间可控，使用 G1 GC。

对于内存大小的考量：

1. 一般 4G 以上，算是比较大，用 G1 的性价比较高。

2. 一般超过 8G，比如 16G-64G 内存，非常推荐使用 G1 GC。

最后讨论一个很多开发者经常忽视的问题，也是面试大厂常问的问题：JDK8 的默认 GC 是什么？（并行GC）

JDK9，JDK10，JDK11…等等默认的是 GC 是什么？（G1 GC）

## ZGC/Shenandoah GC（了解）

#### ZGC介绍

-XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xmx16g

ZGC 最主要的特点包括:

1. GC 最大停顿时间不超过 10ms

2. 堆内存支持范围广，小至几百 MB 的堆空间，大至 4TB 的超大堆内存（JDK13 升至 16TB）

3. 与 G1 相比，应用吞吐量下降不超过 15%

4. 当前只支持 Linux/x64 位平台，JDK15 后支持 MacOS 和Windows 系统

#### Shenandoah GC介绍

-XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -Xmx16g

Shenandoah GC 立项比 ZGC 更早，设计为GC 线程与应用线程并发执行的方式，通过实现垃圾回收过程的并发处理，改善停顿时间，使得 GC 执行线程能够在业务处理线程运行过程中进行堆压缩、标记和整理，从而消除了绝大部分的暂停时间。

Shenandoah 团队对外宣称 Shenandoah GC 的暂停时间与堆大小无关，无论是 200 MB 还是 200 GB的堆内存，都可以保障具有很低的暂停时间（注意:并不像 ZGC 那样保证暂停时间在 10ms 以内）。

## GC总结

到目前为止，我们一共了解了 Java 目前支持的所有 GC 算法，一共有 7 类:

1. 串行 GC（Serial GC）: 单线程执行，应用需要暂停；

2. 并行 GC（ParNew、Parallel Scavenge、Parallel Old）: 多线程并行地执行垃圾回收，关注与高吞吐；

3. CMS（Concurrent Mark-Sweep）: 多线程并发标记和清除，关注与降低延迟；

4. G1（G First）: 通过划分多个内存区域做增量整理和回收，进一步降低延迟；

5. ZGC（Z Garbage Collector）: 通过着色指针和读屏障，实现几乎全部的并发执行，几毫秒级别的延迟，线性可扩展；

6. Epsilon: 实验性的 GC，供性能分析使用；

7. Shenandoah: G1 的改进版本，跟 ZGC 类似。

可以看出 GC 算法和实现的演进路线:

1. 串行 -> 并行: 重复利用多核 CPU 的优势，大幅降低 GC 暂停时间，提升吞吐量。

2. 并行 -> 并发： 不只开多个 GC 线程并行回收，还将GC操作拆分为多个步骤，让很多繁重的任务和应用线程一起并 发执行，减少了单次 GC 暂停持续的时间，这能有效降低业务系统的延迟。

3. CMS -> G1： G1 可以说是在 CMS 基础上进行迭代和优化开发出来的，划分为多个小堆块进行增量回收，这样就更 进一步地降低了单次 GC 暂停的时间

4. G1 -> ZGC：ZGC 号称无停顿垃圾收集器，这又是一次极大的改进。ZGC 和 G1 有一些相似的地方，但是底层的算法 和思想又有了全新的突破。

脱离场景谈性能都是耍流氓”。

目前绝大部分 Java 应用系统，堆内存并不大比如 2G-4G 以内，而且对 10ms 这种低延迟的 GC 暂停不敏感，也就是说处 理一个业务步骤，大概几百毫秒都是可以接受的，GC 暂停 100ms 还是 10ms 没多大区别。另一方面，系统的吞吐量反 而往往是我们追求的重点，这时候就需要考虑采用并行 GC。如果堆内存再大一些，可以考虑 G1 GC。如果内存非常大（比如超过 16G，甚至是 64G、128G），或者是对延迟非常 敏感（比如高频量化交易系统），就需要考虑使用本节提到的新 GC（ZGC/Shenandoah）。

