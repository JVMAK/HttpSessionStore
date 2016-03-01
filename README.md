# HttpSessionStore
����Ŀ�ṩ��Clustered Sessions���ܣ��������õ�Redis���洢Session���ݣ��Դ������Session���������,ʵ��Non-sticky Session��

* ʵ��ԭ��:ͨ��Filter������HttpServletRequestWrapper����getSession()�������ӹܴ����͹���Session�Ĺ�����
* ��������:��Ӧ����������ȫ͸����,֧���ڲ������ǰ����,��Ӧ�õ�session����.
* ������Ŀ:���ƵĿ�Դ��Ŀ��tomcat-redis-session-manager,memcached-session-manager,spring-session�ȡ�
* �� �� ��:Tomcat��Jetty����ͨ��,�����ϼ�������WEB�м����
* ���ģʽ:�۲���,����,װ�Ρ�
* �� չ ��:ͨ��ʵ��com.sessionstore.SessionStoreManager�ӿ�,��������memcached������Nosql��ʵ�֡�

## ʹ��
### maven: ���ȱ��밲װ�����زֿ���߱���˽����
<pre><code>
    &lt;dependency&gt;
      &lt;groupId&gt;dance&lt;/groupId&gt;
      &lt;artifactId&gt;HttpSessionStore&lt;/artifactId&gt;
      &lt;version&gt;0.0.1&lt;/version&gt;
    &lt;/dependency&gt;
</code></pre>

#���÷���һ(QuickStart)
web.xml����
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
	    <!-- �Ự���ǻʱ�䣨��λ�룩 -->
            <param-name>sessionTimeout</param-name>
            <param-value>300</param-value>
        </init-param>
        <init-param>
	    <!-- ����,����:baidu.com �� ���� -->
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
#���÷�����������spring��
Spring����
~~~ xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="sessionStoreFilter" class="com.sessionstore.SessionStoreFilter">
        <property name="sessionStoreManager" ref="redisSessionStoreManager" />
    </bean>

    <bean id="redisSessionStoreManager" class="com.sessionstore.redis.RedisSessionStoreManager" init-method="init">
        <property name="jedisPool" ref="jedisPool" />
        <!-- �Ự���ǻʱ�䣨��λ�룩 -->
        <property name="sessionTimeout" value="300"></property>
        <!-- ����,����:baidu.com �� ����, ���ø�������ʵ�ֿ�Ӧ��session���� -->
        <property name="cookieDomain" value=""></property>
    </bean>


    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg index="0" ref ="jedisConfig"/>
        <constructor-arg index="1" value="127.0.0.1"/>
        <constructor-arg index="2" value="6379" type="int" />
        <constructor-arg index="3" value="5000" type="int" />
    </bean>

    <bean id="jedisConfig" class="redis.clients.jedis.JedisPoolConfig">
        <!-- ��ϸ������ -->
    </bean>

</beans>
~~~
web.xml����
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