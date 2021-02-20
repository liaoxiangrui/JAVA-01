# 学习笔记

## Spring AOP详解（重点）

**AOP-面向切面编程**

Spring早期版本的核心功能，管理对象生命周期与对象装配。 

为了实现管理和装配，一个自然而然的想法就是，加一个中间层代理（**字节码增强**）来实现所有对象的托管。

一个对象的代理有哪些种类？用在什么场景？**接口类型**：默认使用JdkProxy，如果配置参数proxyTargetClass，那就是CGlib；**非接口类型**：默认使用CGlib。

**IoC-控制反转**

也成为DI（Dependency Injection）依赖注入。**对象装配思路的改进**。 

从对象A直接引用和操作对象B，变成对象A里指需要依赖一个接口IB，系统启动和装配阶段，把IB接口的实例对象注入到对象A，这样A就不需要依赖一个IB接口的具体实现，也就是类B。从而可以实现在不修改代码的情况，修改配置文件，即可以运行时替换成注入IB接口另一实现类C的一个对象实例。

什么类型的循环依赖 Spring 无法处理？构造器参数和prototype的setter方式无法处理，因为构造器参数在Bean池中创建Bean还未初始化（Bean初始化完成会从Bean池移除），所以再次创建会报`BeanCurrentlyInCreationException`异常，prototype作用域的Bean，Spring容器无法完成依赖注入，因为Spring容器不进行缓存。

## Spring Bean核心原理（重点）

**Spring Bean生命周期**

Bean的加载过程



Aware 接口有： 

`BeanNameAware`：注入当前 bean 对应 beanName； 

`BeanClassLoaderAware`：注入加载当前 bean 的 ClassLoader； 

`BeanFactoryAware`：注入 当前BeanFactory容器 的引用。 

对于 ApplicationContext 类型的容器（通过 BeanPostProcessor ）：

`EnvironmentAware`：注入 Enviroment，一般用于获取配置属性； 

`EmbeddedValueResolverAware`：注入 EmbeddedValueResolver（Spring EL解析器），一般用于参数解析； 

`ApplicationContextAware`（ResourceLoader、ApplicationEventPublisherAware、MessageSourceAware）：注入 ApplicationContext 容器本身。

`BeanPostProcessor`是 Spring 为修改 bean提供的强大扩展点，其可作用于容器中所有 bean。

`InitializingBean`和`init-method`是 Spring 为 bean 初始化提供的扩展点。

## Spring XML 配置原理（重点）

**XML配置原理：**



解析成Bean后再开始执行Bean的加载过程。

**自动化XML配置工具：**

XmlBeans -> Spring-xbean

2个原理：

1. 根据 Bean 的字段结构，自动生成 XSD
2. 根据 Bean 的字段结构，配置 XML 文件

**Spring简单用法回顾**

纯XML配置开发：没有注解，全部`<bean>`标签，但也可以配置自动装配 

注解开发不能单独存在，需要开启扫描。自动装配一般用`@Autowired` 

XML+注解：XML+`<context:component-scan>`+`@Component` 

JavaConfig+注解：`@Configuration`+`@ComponentScan`+`@Component` 

JavaConfig方式：`@Configuration`+`@Bean`

配置方式：XML、Annotation、JavaConfig

组装方式：ByName，ByType，Lazy

自动装配：Autowired、Resource

注入方式：setter方法、构造方法 

**12种常见注解：**

1. 声明组件的注解 

   - `@Component` 组件，没有明确的角色
   - `@Service` 在业务逻辑层使用（service层）
   - `@Repository` 在数据访问层使用（dao层）
   - `@Controller` 在展现层使用，控制器的声明（C）

2. 注入bean的注解

   - `@Autowired`：由Spring提供

   - `@Inject`：由JSR-330提供

   - `@Resource`：由JSR-250提供

3. Java配置类相关注解

   - `@Configuration` 声明当前类为配置类，相当于xml形式的Spring配置（类上）

   - `@Bean` 注解在方法上，声明当前方法的返回值为一个bean，替代xml中的方式（方法上）

   - `@Component`注解，表明这个类是一个bean（类上）

   - `@ComponentScan` 用于对Component进行扫描，相当于xml中的（类上） 

   - `@WishlyConfiguration` 为`@Configuration`与`@ComponentScan`的组合注解，可以替代这两个注解

4. 切面（AOP）相关注解

   - `@Aspect` 声明一个切面（类上） 

     使用`@After`、`@Before`、`@Around`定义建议（advice），可直接将拦截规则（切点）作为参数。 

   - `@After` 在方法执行之后执行（方法上） 

   - `@Before` 在方法执行之前执行（方法上） 

   - `@Around` 在方法执行之前与之后执行（方法上） 

   - `@PointCut` 声明切点

     在java配置类中使用`@EnableAspectJAutoProxy`注解开启Spring对AspectJ代理的支持（类上）

5. `@Bean`的属性支持

   - `@Scope` 设置Spring容器如何新建Bean实例（方法上，得有`@Bean`）

     其设置类型包括：

     Singleton（单例，一个Spring容器中只有一个bean实例，默认模式）, 

     Prototype（每次调用新建一个bean）, 

     Request（web项目中，给每个http request新建一个bean）, 

     Session（web项目中，给每个http session新建一个bean）, 

     GlobalSession（给每一个 global http session新建一个Bean实例）

   - `@StepScope` 在Spring Batch中还有涉及 

   - `@PostConstruct` 由JSR-250提供，在构造函数执行完之后执行，等价于xml配置文件中bean的initMethod

   - `@PreDestory` 由JSR-250提供，在Bean销毁之前执行，等价于xml配置文件中bean的destroyMethod

6. `@Value`注解：`@Value` 为属性注入值（属性上） 

   支持如下方式的注入： 

   》注入普通字符 `@Value("Michael Jackson")String name`; 

   》注入操作系统属性 `@Value("#{systemProperties['os.name']}")String osName`; 

   》注入表达式结果 `@Value("#{ T(java.lang.Math).random()* 100 }")String randomNumber`; 

   》注入其它bean属性 `@Value("#{domeClass.name}")String name`; 

   》注入文件资源 `@Value("classpath:com/hgs/hello/test.txt")String Resource file`; 

   》注入URL资源 `@Value("http://www.javastack.cn")Resource url`; 

   》注入配置文件 `Value("${book.name}")String bookName`; 

   注入配置使用方法： 

   ① 编写配置文件（test.properties）book.name=《三体》

   ② `@PropertySource` 加载配置文件(类上) `@PropertySource("classpath:com/hgs/hello/test/test.properties")` 

   ③ 还需配置一个PropertySourcesPlaceholderConfigurer的bean。

7. 环境切换

   `@Profile` 通过设定Environment的ActiveProfiles来设定当前context需要使用的配置环境。（类或方法上） 

   `@Conditional` Spring4中可以使用此注解定义条件话的bean，通过实现Condition接口，并重写matches方法，从而决定该bean是否被实例化。（方法上）

8. 异步相关

   `@EnableAsync` 配置类中，通过此注解开启对异步任务的支持，叙事性AsyncConfigurer接口（类上） 

   `@Async` 在实际执行的bean方法使用该注解来申明其是一个异步任务（方法上或类上所有的方法都将异步，需要`@EnableAsync`开启异步任务）

9. 定时任务相关

   `@EnableScheduling` 在配置类上使用，开启计划任务的支持（类上） 

   `@Scheduled` 来申明这是一个任务，包括cron,fixDelay,fixRate等类型（方法上，需先开启计划任务的支持）

10. `@Enable*`注解，这些注解主要用来开启对xxx的支持。

    `@EnableAspectJAutoProxy` 开启对AspectJ自动代理的支持 

    `@EnableAsync` 开启异步方法的支持 

    `@EnableScheduling` 开启计划任务的支持 

    `@EnableWebMvc` 开启Web MVC的配置支持 

    `@EnableConfigurationProperties` 开启对`@ConfigurationProperties`注解配置Bean的支持 

    `@EnableJpaRepositories` 开启对SpringData JPA Repository的支持 

    `@EnableTransactionManagement` 开启注解式事务的支持

    `@EnableCaching` 开启注解式的缓存支持

11. 测试相关注解

    `@RunWith` 运行器，Spring中通常用于对JUnit的支持`@RunWith(SpringJUnit4ClassRunner.class)` 

    `@ContextConfiguration` 用来加载配置ApplicationContext，其中classes属性用来加载配置类 `@ContextConfiguration(classes={TestConfig.class})`

12. SpringMVC相关注解

    `@EnableWebMvc` 在配置类中开启Web MVC的配置支持，如一些ViewResolver或者MessageConverter等，若无此句，重写WebMvcConfigurerAdapter方法（用于对SpringMVC的配置）。 

    `@Controller` 声明该类为SpringMVC中的Controller 

    `@RequestMapping` 用于映射Web请求，包括访问路径和参数（类或方法上） 

    `@ResponseBody` 支持将返回值放在response内，而不是一个页面，通常用户返回json数据（返回值旁或方法上） 

    `@RequestBody` 允许request的参数在request体中，而不是在直接连接在地址后面。（放在参数前） 

    `@PathVariable` 用于接收路径参数，比如`@RequestMapping(“/hello/{name}”)`申明的路径。 

    `@RestController` 该注解为一个组合注解，相当于`@Controller`和`@ResponseBody`的组合。 

    `@ControllerAdvice` 通过该注解，我们可以将对于控制器的全局配置放置在同一个位置。 

    `@ExceptionHandler` 用于全局处理控制器里的异常 

    `@InitBinder` 用来设置WebDataBinder，WebDataBinder用来自动绑定前台请求参数到Model中。 

    `@ModelAttribute` 本来的作用是绑定键值对到Model里，在`@ControllerAdvice`中是让全局的`@RequestMapping`都能获得在此处设置的键值对。

## Spring MVC实现REST（重点）

**什么是MVC**



**SpringMVC的流程**：

第一步：用户发起请求到前端控制器（DispatcherServlet） 

第二步：前端控制器请求处理器映射器（HandlerMappering）去查找处理器（Handle）,通过xml配置或者注解查找 

第三步：找到以后处理器映射器（HandlerMappering）像前端控制器返回执行链（HandlerExecutionChain） 

第四步：前端控制器（DispatcherServlet）调用处理器适配器（HandlerAdapter）去执行处理器（Handler） 

第五步：处理器适配器去执行Handler 

第六步：Handler执行完给处理器适配器返回ModelAndView 

第七步：处理器适配器向前端控制器返回ModelAndView 

第八步：前端控制器请求视图解析器（ViewResolver）去进行视图解析 

第九步：视图解析器像前端控制器返回View 

第十步：前端控制器对视图进行渲染 

第十一步：前端控制器向用户响应结果

**从Spring MVC走向前后端分离REST**

前端：三大框架（Vue、React、Angular）

后端：SpringMVC->RestController，SwaggerUI



## Spring Data/Messaging（了解）

**Spring Data**：

Spring 的一个子项目。用于简化数据库访问，支持NoSQL和关系数据库存储。其主要目标是使数据库的访问变得方便快捷。 

Spring Data 项目所支持NoSQL存储（通过统一抽象，像操作数据库一样）： 

\- - MongoDB（文档数据库） 

\- - Neo4j （图形数据库） 

\- - Redis（键/值存储） 

\- - Hbase（列族数据库） 

Spring Data 项目所支持的关系数据存储： 

\- - JDBC

\- - JPA

**Spring Messaging**

简化消息操作，实现面向对象一样的方式去发送和接收消息 

XXXTemplate：如JMSTemplate，KafkaTemplate，AmqpTemplate 

Converter：转换Pojo与Message对象

## 从 Spring 到 Spring Boot（了解）

**什么是Spring Boot**

Spring Boot 使创建独立运行、生产级别的 Spring 应用变得容易，你可以直接运行它。我们对 Spring 平台和第三方库采用限定性视角，以此让大家能在最小的成本下上手。大部分 Spring Boot 应用仅仅需要最少量的配置。

功能特性：

1. 创建独立运行的 Spring 应用

2. 直接嵌入 Tomcat 或 Jetty，Undertow，无需部署 WAR 包

3. 提供限定性的 starter 依赖简化配置（就是脚手架）

4. 在必要时自动化配置 Spring 和其他三方依赖库

5. 提供生产 production-ready 特性，例如指标度量，健康检查，外部配置等

6. 完全零代码生产和不需要 XML 配置

**Spring Boot如何做到简化**

基于什么变简单：**约定大于配置**。

为什么能做到简化：

1. Spring 本身技术的成熟与完善，各方面第三方组件的成熟集成
2. Spring 团队在去 web 容器化等方面的努力
3. 基于 MAVEN 与 POM 的 Java 生态体系，整合 POM 模板成为可能
4. 避免大量 maven 导入和各种版本冲突

Spring Boot 是 Spring 的一套快速配置脚手架，关注于自动配置，配置驱动。

## Spring Boot 核心原理（重点）

**Spring Boot两大核心原理**

1. 自动化配置：简化配置核心，基于 Configuration，EnableXX，Condition
2. spring-boot-starter：脚手架核心，整合各种第三方类库，协同工具

**为什么要约定大于配置**

举例来说，JVM 有1000多个参数，但是我们不需要一个参数，就能 java Hello。

优势在于，开箱即用：

1. Maven 的目录结构：默认有 resources 文件夹存放配置文件。默认打包方式为 jar。
2. 默认的配置文件：application.properties 或 application.yml 文件
3. 默认通过 spring.profiles.active 属性来决定运行环境时的配置文件。
4. EnableAutoConfiguration 默认对于依赖的 starter 进行自动装载。
5. spring-boot-start-web 中默认包含 spring-mvc 相关依赖以及内置的 web容器，使得构建一个 web 应用更加简单。

**Spring Boot 自动配置注解**

- `@SpringBootApplication`

  SpringBoot 应用标注在某个类上说明这个类是 SpringBoot 的主配置类，SpringBoot 就会运行这个类的 main 方法来启动 SpringBoot 项目。

  - `@SpringBootConfiguration`
  - `@EnableAutoConfiguration`
  - `@AutoConfigurationPackage`
  - `@Import({AutoConfigurationImportSelector.class})`

加载所有 META-INF/spring.factories 中存在的配置类（类似 SpringMVC 中加载所有 converter）

**自动配置机制和装配方法**

1. 通过各种注解实现了类与类之间的依赖关系，容器在启动的时候Application.run，会调用AutoConfigurationImportSelector.class的selectImports方法（其实是其父类的方法）
2. selectImports方法最终会调用SpringFactoriesLoader.loadFactoryNames方法来获取一个全面的常用BeanConfiguration列表 
3. loadFactoryNames方法会读取FACTORIES_RESOURCE_LOCATION（也就是spring-boot-autoconfigure.jar 下面的spring.factories），获取到所有的Spring相关的Bean的全限定名ClassName，大概120多个
4. selectImports方法继续调用filter(configurations, autoConfigurationMetadata);这个时候会根据这些BeanConfiguration里面的条件，来一一筛选，最关键的是@ConditionalOnClass，这个条件注解会去classpath下查找，jar包里面是否有这个条件依赖类，所以必须有了相应的jar包，才有这些依赖类，才会生成IOC环境需要的一些默认配置Bean
5. 最后把符合条件的BeanConfiguration注入默认的EnableConfigurationPropertie类里面的属性值，并且注入到IOC环境当中

**Spring Boot最核心的25个注解**

1. `@SpringBootApplication`

   这是 Spring Boot 最最最核心的注解，用在 Spring Boot 主类上，标识这是一个 Spring Boot 应用，用来开启Spring Boot 的各项能力。其实这个注解就是`@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan`这三个注解的组合，也可以用这三个注解来代替 `@SpringBootApplication` 注解。

2. `@EnableAutoConfiguration`

   允许 Spring Boot 自动配置注解，开启这个注解之后，Spring Boot 就能根据当前类路径下的包或者类来配置 Spring Bean。如：当前类路径下有 Mybatis 这个 JAR 包，MybatisAutoConfiguration 注解就能根据相关参数来配置 Mybatis 的各个 Spring Bean。

3. `@Configuration`

   这是 Spring 3.0 添加的一个注解，用来代替 applicationContext.xml 配置文件，所有这个配置文件里面能做到的事情都可以通过这个注解所在类来进行注册。

4. `@SpringBootConfiguration`

   这个注解就是 `@Configuration` 注解的变体，只是用来修饰是 Spring Boot 配置而已，或者可利于 Spring Boot 后续的扩展。

5. `@ComponentScan`

   这是 Spring 3.1 添加的一个注解，用来代替配置文件中的 component-scan 配置，开启组件扫描，即自动扫描包路径下的 `@Component` 注解进行注册 bean 实例到 context 中。

6. `@Conditional`

   这是 Spring 4.0 添加的新注解，用来标识一个 Spring Bean 或者 Configuration 配置文件，当满足指定的条件才开启配置。

7. `@ConditionalOnBean`

   组合 `@Conditional` 注解，当容器中有指定的 Bean 才开启配置。

8. `@ConditionalOnMissingBean`

   组合 `@Conditional` 注解，和 `@ConditionalOnBean` 注解相反，当容器中没有指定的 Bean 才开启配置。

9. `@ConditionalOnClass`

   组合 `@Conditional` 注解，当容器中有指定的 Class 才开启配置。

10. `@ConditionalOnMissingClass`

    组合 `@Conditional` 注解，和 `@ConditionalOnClass` 注解相反，当容器中没有指定的 Class 才开启配置。

11. `@ConditionalOnWebApplication`

    组合 `@Conditional` 注解，当前项目类型是 WEB 项目才开启配置。 

    当前项目有以下 3 种类型。 

    ```java
    enum Type { 
    	/** 
    	 * Any web application will match. 
    	 */
    	ANY, 
    	/**
         * Only servlet-based web application will match. 
         */
    	SERVLET, 
    	/**
         * Only reactive-based web application will match.
         */
    	REACTIVE
    }
    ```

12. `@ConditionalOnNotWebApplication`

    组合 `@Conditional` 注解，和 `@ConditionalOnWebApplication` 注解相反，当前项目类型不是 WEB 项目才开启配置。

13. `@ConditionalOnProperty`

    组合 `@Conditional` 注解，当指定的属性有指定的值时才开启配置。

14. `@ConditionalOnExpression`

    组合 `@Conditional` 注解，当 SpEL 表达式为 true 时才开启配置。 

15. `@ConditionalOnJava`

    组合 `@Conditional` 注解，当运行的 Java JVM 在指定的版本范围时才开启配置。

16. `@ConditionalOnResource`

    组合 `@Conditional` 注解，当类路径下有指定的资源才开启配置。

17. `@ConditionalOnJndi`

    组合 `@Conditional` 注解，当指定的 JNDI 存在时才开启配置。

18. `@ConditionalOnCloudPlatform`

    组合 `@Conditional` 注解，当指定的云平台激活时才开启配置。

19. `@ConditionalOnSingleCandidate`

    组合 `@Conditional` 注解，当指定的 class 在容器中只有一个 Bean，或者同时有多个但为首选时才开启配置。

20. `@ConfigurationProperties`

    用来加载额外的配置（如 .properties 文件），可用在 `@Configuration` 注解类，或者`@Bean` 注解方法上面。关于这个注解的用法可以参考《Spring Boot读取配置的几种方式》这篇文章。

21. `@EnableConfigurationProperties`

    一般要配合 `@ConfigurationProperties` 注解使用，用来开启对`@ConfigurationProperties` 注解配置 Bean 的支持。

22. `@AutoConfigureAfter`

    用在自动配置类上面，表示该自动配置类需要在另外指定的自动配置类配置完之后。如 Mybatis 的自动配置类，需要在数据源自动配置类之后。

23. `@AutoConfigureBefore`

    这个和 `@AutoConfigureAfter` 注解使用相反，表示该自动配置类需要在另外指定的自动配置类配置之前。

24. `@Import`

    这是 Spring 3.0 添加的新注解，用来导入一个或者多个 `@Configuration` 注解修饰的类。

25. `@ImportResource`

    这是 Spring 3.0 添加的新注解，用来导入一个或者多个 Spring 配置文件，这对 Spring Boot 兼容老项目非常有用，因为有些配置无法通过 Java Config 的形式来配置就只能用这个注解来导入。 

## Spring Boot Starter 详解（重点）

**自定义Spring Boot Starter**

一个完整的Spring Boot Starter可能包含以下组件：

- autoconfigure模块：包含自动配置的代码

- starter模块：提供对autoconfigure模块的依赖，以及一些其它的依赖

1. 命名 

   模块名称不能以spring-boot开头，如果你的starter提供了配置keys，那么请确保它们有唯一的命名空间。而且，不要用Spring Boot用到的命名空间（比如：server， management， spring 等等），举个例子，假设你为“acme”创建了一个starter，那么你的auto-configure模块可以命名为acme-spring-boot-autoconfigure，starter模块可以命名为acme-spring-boot-starter。如果你只有一个模块包含这两部分，那么你可以命名为acme-spring-boot-starter。

2. autoconfigure模块 

   建议在autoconfigure模块中包含下列依赖： 

   ```xml
   <dependency>
   	<groupId>org.springframework.boot</groupId>
   	<artifactId>spring-boot-autoconfigure-processor</artifactId> 
   	<optional>true</optional>
   </dependency>
   ```

3. starter模块 

   事实上，starter是一个空jar。它唯一的目的是提供这个库所必须的依赖。你的starter必须直接或间接引用核心的Spring Boot starter（spring-boot-starter）。

## ORM-Hibernate/MyBatis（重点）

**Hibernate**

ORM（Object-Relational Mapping） 表示对象关系映射。

Hibernate 是一个开源的对象关系映射框架，它对JDBC 进行了非常轻量级的对象封装，它将 POJO 与数据库表建立映射关系，是一个全自动的 orm 框架，hibernate 可以自动生成 SQL 语句，自动执行，使得 Java 程序员可以使用面向对象的思维来操纵数据库。

Hibernate 里需要定义实体类和 hbm 映射关系文件（IDE 一般有工具生成）。

Hibernate 里可以使用 HQL、Criteria、Native SQL三种方式操作数据库。也可以作为 JPA 适配实现，使用 JPA 接口操作。



**MyBatis**

MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java 对象)映射成数据库中的记录。



**MyBatis-半自动化ORM**

1. 需要使用映射文件 mapper.xml 定义 map规则和 SQL
2. 需要定义 mapper/DAO，基于 xml 规则，操作数据库

可以使用工具生成基础的 mapper.xml 和 mapper/DAO，一个经验就是，继承生成的 mapper，而不是覆盖掉。也可以直接在 mapper 上用注解方式配置 SQL。

**MyBatis** **与** **Hibernate** **比较**

MyBatis 与 Hibernate 的区别与联系？

Mybatis 优点：原生 SQL（XML 语法），直观，对 DBA 友好

Hibernate 优点：简单场景不用写 SQL（HQL、Cretiria、SQL）

Mybatis 缺点：繁琐，可以用 MyBatis-generator、MyBatis-Plus 之类的插件

Hibernate 缺点：对 DBA 不友好

## Spring 集成 ORM 与 JPA（重点）

**JPA**

JPA 的全称是 Java Persistence API，即 Java 持久化 API，是一套基于 ORM 的规范，内部是由一系列的接口和抽象类构成。JPA 通过 JDK 5.0 注解描述对象-关系表映射关系，并将运行期的实体对象持久化到数据库中。核心 EntityManager



**Spring 管理事务**

事务的特性：

- 原子性（Atomicity）：事务是一个原子操作，由一系列动作组成。事务的原子性确保动作要么全部完成，要么完全不起作用。 
- 一致性（Consistency）：一旦事务完成（不管成功还是失败），系统必须确保它所建模的业务处于一致的状态，而不会是部分完成部分失败。在现实中的数据不应该被破坏。 
- 隔离性（Isolation）：可能有许多事务会同时处理相同的数据，因此每个事务都应该与其他事务隔离开来，防止数据损坏。
- 持久性（Durability）：一旦事务完成，无论发生什么系统错误，它的结果都不应该受到影响，这样就能从任何系统崩溃中恢复过来。通常情况下，事务的结果被写到持久化存储器中。

JDBC 层，数据库访问层，怎么操作事务？编程式事务管理

Spring 怎么做到无侵入实现事务？声明式事务管理：事务管理器+AOP



事务的传播性一般用在事务嵌套的场景，比如一个事务方法里面调用了另外一个事务方法，那么两个方法是各自作为独立的方法提交还是内层的事务合并到外层的事务一起提交，这就是需要事务传播机制的配置来确定怎么样执行。 

常用的事务传播机制如下： 

- PROPAGATION_REQUIRED：Spring默认的传播机制，能满足绝大部分业务需求，如果外层有事务，则当前事务加入到外层事务，一块提交，一块回滚。如果外层没有事务，新建一个事务执行
- PROPAGATION_REQUES_NEW：该事务传播机制是每次都会新开启一个事务，同时把外层事务挂起，当当前事务执行完毕，恢复上层事务的执行。如果外层没有事务，执行当前新开启的事务即可
- PROPAGATION_SUPPORT：如果外层有事务，则加入外层事务，如果外层没有事务，则直接使用非事务方式执行。完全依赖外层的事务
- PROPAGATION_NOT_SUPPORT：该传播机制不支持事务，如果外层存在事务则挂起，执行完当前代码，则恢复外层事务，无论是否异常都不会回滚当前的代码
- PROPAGATION_NEVER：该传播机制不支持外层事务，即如果外层有事务就抛出异常
- PROPAGATION_MANDATORY：与NEVER相反，如果外层没有事务，则抛出异常
- PROPAGATION_NESTED：该传播机制的特点是可以保存状态保存点，当前事务回滚到某一个点，从而避免所有的嵌套事务都回滚，即各自回滚各自的，如果子事务没有把异常吃掉，基本还是会引起全部回滚的。

**Spring 声明式事务配置参考**

事务的传播性：

`@Transactional(propagation=Propagation.REQUIRED)`

事务的隔离级别：

`@Transactional(isolation = Isolation.READ_UNCOMMITTED)`

读取未提交数据(会出现脏读, 不可重复读) 基本不使用

只读：

`@Transactional(readOnly=true)`

该属性用于设置当前事务是否为只读事务，设置为 true 表示只读，false 则表示可读写，默认值为 false。

事务的超时性：

`@Transactional(timeout=30)`

回滚：

指定单一异常类：`@Transactional(rollbackFor=RuntimeException.class)`

指定多个异常类：`@Transactional(rollbackFor={RuntimeException.class, Exception.class})`

**Spring/Spring Boot 使用 ORM 的经验**

1. 本地事务（事务的设计与坑）
2. 多数据源（配置、静态制定、动态切换）
3. 线程池配置（大小：经验值50-100、重连、超时）
4. ORM 内的复杂 SQL，级联查询
5. ORM 辅助工具和插件

## Spring Boot 拓展进阶

启动Spring Boot项目时传递参数，有三种参数形式： 

- 选项参数
- 非选项参数
- 系统参数 

ApplicationArguments解析

Spring boot Actuator监控功能

**Spring Boot最佳实践** 

1. 代码结构：

   避免使用默认包，如果创建的类没有声明包信息，则类会默认使用默认包，默认包使用在使用诸如`@ComponentScan`, `@EntityScan`, 及`@SpringBootApplication`时可能会引发特殊问题。官方建议遵循java既有的命名约定规则，使用反转域名的方式命名包。例如，`com.example.project`. 

2. 应用主类位置：

   通常我们建议将主类放置于根路径下，注解`@SpringBootApplication` 通常放置于主类上，并且作为某些扫描的根路径。如JPA配置的Entity扫描等。`@SpringBootApplication`注解包含 `@EnableAutoConfiguration` 和 `@ComponentScan` ，可以单独配置，或者直接使用`@SpringBootApplication` 简化配置。

3. 配置类`@Configuration`：

   Spring boot倾向使用基于java配置类的配置方式，建议使用主类作为主要的配置位置`@Configuration`。 

4. 引入额外的配置类：不需要将所有的配置放到一个配置类中，可以通过使用`@Import`注解引入额外的配置类信息。当然`@ComponentScan`注解会扫描包含`@Configuration`注解的配置类。 

5. 引入xml配置：如果存在不许使用xml配置的情况，则可以通过`@ImportResource`注解来进行加载。 

6. 自动配置`@EnableAutoConfiguration`

   Spring boot基于添加的相应的功能jar进行自动配置。例如，类路径中有HSQLDB jar包的情况下，如果没有主动定义相应的数据源连接bean，则spring boot会自动配置内存数据库。自动配置需添加相应的`@EnableAutoConfiguration`或者`@SpringBootApplication`来启用。通常放置其一于主类即可。

7. 自动配置的覆盖：

   自动配置是非侵入性的，可以通过定义相应的自定义配置类进行覆盖，如果需要知道工程目前使用了那些自动配置，可以通过在启动时添加—debug选项，来进行输出。 

8. 禁用某些自动配置

   如果发现输出的日志中包含一些不需要应用的自动配置可以通过在注解`@EnableAutoConfiguration`上添加exclude附加选项来禁用。 

9. maven打包后为fat jar：

   mvn clean package

   使用maven插件运行：

   $ mvn spring-boot:run