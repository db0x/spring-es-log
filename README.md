# spring-es-log
Integration of direct ElasticSearch-Log Appender in spring-boot

bases on https://github.com/internetitem/logback-elasticsearch-appender

motivation : use elasticsearch-appender without logback.xml 
cofiguration in appication.yml of the spring boot container


application.yml exmpl:
```yml
server:
  port: 8080
   
spring:
  application:
    name: myApp
    log:
      enable-es-log: true
      es-log-url: http://localhost:9200/_bulk
      index-name: log-%date{yyyy-MM-dd}
      type: myApplog
      parameters:        
        host: \%X{host}
        port: \%X{port}
        severity: \%level
        thread: \%thread
        logger: \%logger
        stacktrace: \%ex
        application: \%X{spring.application.name}
```
