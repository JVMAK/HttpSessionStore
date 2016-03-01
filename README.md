# HttpSessionStore
本项目提供了Clustered Sessions功能，采用外置的Redis来存储Session数据，以此来解决Session共享的问题。 
对应用来讲是完全透明的.

## 使用
### maven: 需先编译安装到本地仓库或者本地私服。
<pre><code>
    &lt;dependency&gt;
      &lt;groupId&gt;dance&lt;/groupId&gt;
      &lt;artifactId&gt;HttpSessionStore&lt;/artifactId&gt;
      &lt;version&gt;0.0.1&lt;/version&gt;
    &lt;/dependency&gt;
</code></pre>

#配置方法一
web.xml
~~~ xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >
<web-app>
  <display-name>demo</display-name>
    <filter>
        <filter-name>SessionStoreFilter</filter-name>
        <filter-class>com.sessionstore.SessionStoreFilter</filter-class>
        <init-param>
            <param-name>redisIp</param-name>
            <param-value>127.0.0.1</param-value>
        </init-param>
        <init-param>
            <param-name>redisPort</param-name>
            <param-value>6379</param-value>
        </init-param>
        <init-param>
            <param-name>redisTimeout</param-name>
            <param-value>5000</param-value>
        </init-param>
        <init-param>
	    <!-- 会话最大非活动时间（单位秒） -->
            <param-name>sessionTimeout</param-name>
            <param-value>300</param-value>
        </init-param>
        <init-param>
	    <!-- 域名,例如:baidu.com 或 留空 -->
            <param-name>cookieDomain</param-name>
            <param-value></param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>SessionStoreFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
~~~
#配置方法二（采用spring管理redis连接池）
spring配置
~~~ xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="sessionStoreFilter" class="com.sessionstore.SessionStoreFilter">
        <property name="sessionStoreManager" ref="redisSessionStoreManager" />
    </bean>

    <bean id="redisSessionStoreManager" class="com.sessionstore.redis.RedisSessionStoreManager" init-method="init">
        <property name="jedisPool" ref="jedisPool" />
        <!-- 会话最大非活动时间（单位秒） -->
        <property name="sessionTimeout" value="300"></property>
        <!-- 域名,例如:baidu.com 或 留空 -->
        <property name="cookieDomain" value=""></property>
    </bean>


    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg index="0" ref ="jedisConfig"/>
        <constructor-arg index="1" value="127.0.0.1"/>
        <constructor-arg index="2" value="6379" type="int" />
        <constructor-arg index="3" value="5000" type="int" />
    </bean>

    <bean id="jedisConfig" class="redis.clients.jedis.JedisPoolConfig">
        <!-- 详细配置略 -->
    </bean>

</beans>
~~~
web.xml配置
~~~ xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >
<web-app>
  <display-name>Archetype Created Web Application</display-name>
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            classpath:applicationContext.xml
        </param-value>
    </context-param>
    <filter>
        <filter-name>SessionStoreFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <param-name>targetBeanName</param-name>
            <param-value>sessionStoreFilter</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>SessionStoreFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
~~~

注意事项:
通过session.setAttribute方法存入的数据,会被序列化,故通过session.getAttribute方法获取的对象,一旦有修改,必须再次调用session.setAttribute方法重新保存.
例如:
~~~ java
User user=(User)session.getAttribute("user");
user.setName("新名字");//修改了对象的属性后必须重新存入redis.
session.setAttribute("user",user);
~~~ 