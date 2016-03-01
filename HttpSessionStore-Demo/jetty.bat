SET MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=7777
call mvn clean jetty:run -Djetty.port=8080
pause