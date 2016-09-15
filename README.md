# 不是简单的造轮子

**编程 Java JavaScript 框架 提升**

说明
----

我日常学习和工作所使用的语言是Java，JavaScript，获取知识的主要途径是书本，相关经典书基本上都读过，不过记性不好，忘得快，要将知识活学活用，还得不断实践，所以我通过创建一些代码库来巩固知识体系。

本项目是我锻炼基本编程水平的代码库，实现的功能早已有相关框架或工具所支持，如MVC，IOC，json解析等，这属于“重复造轮子”，在实际项目中更多的还是选用工业标准的第三方软件，事实上我模仿的框架就是Spring，jquery，requirejs，编写它们纯粹是个人提升和自娱自乐。


本项目按java后台功能划分为5个模块：

-   frame-util ：为其他模块提供基础工具功能，如**解析json**，**对象分析**，HTTP**客户端**，**Class扫描**等；

-   frame-ioc ：如同spring容器那样，实现了**反转控制**和**依赖注入**功能，作为应用程序中对象的容器，它可将Bean单例化，并为Bean提供代理，在切面处增强功能，不过目前只支持事务，此外，这样的框架也能很好地解耦模块之间的关系，并让单元测试更为简单；

-   frame-dao ：JPA的映射关系太过于复杂，本组件只将注意集中在简单的查询SQL上，它可以分析PO对象，动态地生成SQL语句，然后提交到JDBC接口中，此外，本组件模仿**spring**的**JdbcTemplate**对于JDBC进行简单封装；

-   frame-mvc ：模拟**spring**的**mvc**，能支持**RestFull**风格，提供GET/POST/PUT/DELETE的调度；

-   frame-frontend ：该模块以一个示例项目来整合各个组件，此外，在前端，收集了常用Javascript工具库，并且实现了AMD管理风格，主要模拟**RequireJS**的依赖注入。

经过多次失败尝试后，现在，frame-ioc项目终于实现了反转控制和依赖注入功能，本项目集合已基本具备框架功能，主要目标已经达到，不过反观之Java EE规范，这些特性仍然只是冰山一角。

本项目，除了样式上使用了bootstrap以外，一切基础底层功能均由自己实现，虽自己尽力完善，但毕竟是个人实践项目，并未经过广泛检验。

在今后的学习中，我将更多地专注于另一个不断完善的企业级应用：web-building，因为那个项目才是整合了业界成熟规范和框架的项目，在该项目上可以开发出大型应用。

最后，__2016年中秋节快乐！__

================

## 1  下载与导入
本项目是聚合的maven项目，先git clone到本地，然后用eclipse导入，再import为maven。
frame-frontend作为入口，也是示例的“业务代码”，它使用两个数据源，代码会定时从一个数据源中的供销商表同步到本地数据库中，sql脚本位于src/main/resources中，按照配置创建数据源，开发中使用的数据库产品是postgresql。
> 说明，需要首先建立数据库，否则项目启动时会自动同步“远程”数据到“本地”数据库上。

## 2 frame-frontend

### 2.1 启动

本项目并没有在“web.xml”中配置servlet、filter以及listener，而是采用流行的编程风格配置，位于“com.github.emailtohl.frame.site.Boot”类中，最主要的是就是配置frame-mvc模块中的DispatcherServlet，该调度器模仿Spring的同名控制器，在向容器注册时，可这样使用：
```java
DispatcherServlet dispatcherServlet = new DispatcherServlet("com.github.emailtohl.frame.site.controller", "/WEB-INF/jsp/");
/* 将ioc容器较于mvc管理 */
dispatcherServlet.setContext(ctx);
ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
dispatcher.setLoadOnStartup(1);
/* 可以上传文件 */
dispatcher.setMultipartConfig(new MultipartConfigElement(null, 20_971_5200L, 41_943_0400L, 512_000));
dispatcher.addMapping("/");
```
构造器中，第一个参数是扫描具有@Mvc注解的包，第二个参数是默认JSP的位置。至于过滤器的注册，可以见实际代码。

### 2.2 Java代码-授权与认证

启动项目后，需登录，“认证”和“授权”功能没有存储在数据库，而是简单地写在程序里面，其中认证的数据写在“com.github.emailtohl.frame.site.controller.Login”中，而“授权”的功能写在过滤器“com.github.emailtohl.frame.site.filter.AuthenticationFilter”，该过滤器也是简单角色权限控制过滤器。

### 2.3 Java代码-Controller

在“com.github.emailtohl.frame.site.controller”包中使用了frame-mvc中的组件，@Mvc注解提供了URL+METHOD与方法的映射关系。

下面是符合Restful风格的控制器
```java
@Mvc(action = "goods/model", method = GET)
public List<GoodsDto> model(GoodsDto goodsDto) {
	return goodsService.queryGoods(goodsDto);
}
```
上面示例返回的是一个对象，DispatcherServlet将使用frame-util中“com.github.emailtohl.frame.util. Serializing”实现的Json解析功能返回到前端。

不过对于PUT方法，容器似乎并不像POST那样去分析“application/x-www-form-urlencoded”数据，所以在frame-util组件中提供了一个自定义方法“com.github.emailtohl.frame.util.ServletUtils.parseForm()”，它将从流中获取表单数据。

控制器在调用service方法时，往往需要事务性的操作数据库，在“frame-dao”中的“com.github.emailtohl.frame.transition.TransitionProxy.getProxy.getProxy()”可以获取service的动态代理支持事务。

### 2.4 Javascript代码-util

作为js的基础工具库，提供诸如ajax、分页组件、table排序等等有趣功能，支持Promise风格，使用方法可以见业务代码。比如以下几种应用：

**在Js控制器中的ajax调用**
```javascript
document.getElementById('multiUpload').onsubmit = function(event) {
	event.preventDefault();
	var promise = util.post('file/multiUpload', this, null, progressListener);
	promise.success(function(xhr) {
		alert(xhr.responseText);
	}).error(function(xhr) {
		console.log(xhr);
	alert('上传失败，检查是否超出容量，有无权限');
	});
};
```
第一个参数是上传地址，第二个参数是表单元素，第三个参数是回调函数，由于使用promise风格，所以这里就不传回调函数了，第四个参数是进度处理函数。

**使用分页组件**
```javascript
util.createPageItems(nav, totalPage, pageNum).onclick(function(pageNum) {
	app.selectOne('input[name="pageNum"]').value = pageNum;
        app.selectOne('form#goods-query input[type="submit"]').click();
});
```
**table排序**
```javascript
util.makeSortable(document.querySelector('table'));
```
将table元素传入方法参数即可，执行后，点击table下面的<th><tr></tr></th>元素就可以按该列内容进行排序
### 2.5 Javascript代码-context

实现了AMD风格，使用方法与RequireJS相似，不过Context是个构造函数，define，require方法成为该构造函数的实例方法，例如：
```javascript
var app = new Context('resource/scripts/');// 告诉其root目录
app.define('goods/controller', ['goods/service', 'common/validate', 'goods/add', 'goods/edit'],
		function(service, validation, addModule, editModule) {
	return function() {
	// ...
	}
});
```
需要注意的是，不能像RequireJS那样省略第一个参数，这里第一个参数'goods/controller'将告诉Context本模块的名字以及所处的目录位置。

## 3 frame-util
该组件提供一些基础功能，下面将简单介绍。

### 3.1 BeanUtils
主要是分析对象的字段（以下称：field）或属性（JavaBean规范中的Property），为框架的基础功能。
- public static Map<String, Object> getFieldMap(Object bean);
分析对象的field，并以Map形式返回键值对。
- public static <T> T deepCopy(T src);
实现对象深度clone功能，可复制对象网
- public static void injectFieldWithString(Field field, Object bean, String value); 将字符串注入到对象的field中，主要用于MVC将表单数据还原为对象
- public static <T> T merge(T dest, Object... srcs); 将同名同类型的field进行合并

### 3.2 CommonUtils
事实上本类收集一些没有归类的工具方法，所以并没有多少内容，受Javascript中字符串join启发：
public static String join(Collection<String> collection, String separator);
弥补Java字符串中只有split没有join的空白。
### 3.3 WebAccess和HttpsRequest
前者以JDK的URLConnection为基础实现基本的HTTP，GET、POST请求，后者继承前者，并在其基础上实现HTTPS访问功能。在微信公众平台第三方应用的开发中，与微信服务器交互可使用本类。
### 3.4 Serializable
仿GSON实现对象和JSON的转换功能
public String toJson(Object obj); 将对象序列化为JSON字符串：
```
graph LR
Object-->JSON
```
public Object fromJson(String json); 将JSON字符串解析为Map<String, Object>或List<Object>，在业务代码中，需要根据JSON结构进行强转。

```
graph LR
JSON-->Map或List
```
本类同时实现了将对象序列化为xml的功能，对于反序列化来说，xml的解析早已纳入Javase中，JDK自带DOM和SAX支持，再加上XML规范众多，故未再提供反序列化功能。

### 3.5 PackageScanner
来源于网上，该方法可以扫描包或JAR文件，将其之下的所有类存放在集合中：Set<Class<?>>。这样frame-mvc组件中就可以将标注@Mvc的类纳入管理中。

### 3.6 MyExecutor
实现Executor的执行器接口中**线程池**的功能，可在构造器中传入最大线程数。由于线程中的bug不易跟踪，难以察觉，所以本类主要作为实验性质，学习之用，JDK自身已提供了丰富的实现，如：CachedThreadPool,FixedThreadPool,SingleThreadExecutor，它们才是开发时所选用。

# 4 frame-dao
事实上JavaEE在持久层的规范是JPA，但要实现像Hibernate那样的功能，非常困难，所以本组件主要面向JDBC，并提供基本的SQL分析功能，在继承BaseDao的类中，可以方便使用增、删、改功能：
- public long insert(Object po); 将po对象插入到数据表中
- public int delete(Object conditionPo); 删除该po对应的数据库记录
- public int update(Object po, Object conditionPo);； 更新记录，where条件满足conditionPo各字段的值

对于查询来说，SQL需要根据实际情况来编写，不过可以用工具将conditionPo分析为SQL谓词（predicate）部分简化**动态查询**的实现。

### 4.1 datasource
该包下采用**策略模式**对javax.sql.DataSource进行封装：当线程在service层时获取真实的Datasource，并将该DataSource缓存到ThreadLocal上。当线程在dao层时，再从ThreadLocal上获取DataSource。关闭DataSource的方式类似，如此实现了事务功能。
### 4.2 myjdbctemplate
其中SimpleJdbcTemplate模仿了Spring的JdbcTemplate，而BeanAnnotationRowMapper<T>可以支持SimpleJdbcTemplate的查询功能，并将JDBC返回的ResultSet注入到PO对象中。
### 4.3 preparedstatementfactory
PoAnalyzer先分析PO对象，扫描自定义注解或JPA注解，并将关键信息存入PropertyBean对象中，然后SqlBuilder对PO对象进行分析，生成可使用的SQL语句。

# 5 frame-mvc
实际上在frame-frontend中已经介绍过如何使用本组件提供的MVC功能，这里简单介绍一下组件内部结构。

首先，MvcParser将使用frame-util中的PackageScanner，扫描包下标记@Mvc注解的“控制器”。

然后将映射关系存入ClassAndMethodBean对象中。

当前端访问时，DispatcherServlet根据前端的请求选择调用具体的方法。

# 6 frame-ioc
项目中 com.github.emailtohl.frame.ioc.Component注解仿Spring中的同名注解，只是Spring中使用value来定义Bean的name，而本注解使用name来定义。

com.github.emailtohl.frame.ioc.InstanceModel是容器管理Bean所建立的模型类。

com.github.emailtohl.frame.ioc.Context 即容器类，它可以在调用构造器public Context(String packagePath)时，将指定包目录下所有注解了Component的类单例话，并根据javax.inject.Inject在构造器、Setter方法或Field字段上的注解，提供依赖注入功能。

当然用户也可以先调用无参构造器，然后再注册Bean对象，如下两种方式：
```java
Context c = new Context();
c.register("com.github.emailtohl.frame.ioc.testsite");
assertNotNull(c.getInstance("someController"));
```

或

```java
Context c = new Context();
OtherUtil otherUtil = new OtherUtil();
c.register("otherUtil", otherUtil);
```

使用容器中的Bean和Spring类似，可以通过name（id），也可以通过Bean的Class对象获取，例如获取某接口的实现：

```java
c.getInstance(SomeService.class);
```
具体使用，可见项目中的使用方式。
