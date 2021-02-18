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

