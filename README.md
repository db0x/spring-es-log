# spring-es-log
Integration of direct ElasticSearch-Log Appender in spring-boot

bases on https://github.com/internetitem/logback-elasticsearch-appender

motivation : use elasticsearch-appender without logback.xml 
cofiguration in appication.yml of the spring boot container


application.yml exmpl:

```yml
server:
  port: 8080 # is required if you use parameters.port
   
spring:
  application:
    name: myApp # is required if you use parameters.application
    log:
      enable-es-log: true # default is true
      es-log-url: http://localhost:9200/_bulk # default is localhost:9200/_bulk
      index-name: log-%date{yyyy-MM-dd} # default is log-%date{yyyy-MM-dd} 
      type: myApplog # optional
      parameters:        
        host: \%X{host} #optional will print name of the host
        port: \%X{port} #optional will print server.port if set
        severity: \%level #optional default is INFO
        thread: \%thread #default is \%thread
        logger: \%logger #default is \%logger
        stacktrace: \%ex #default is \%ex
        application: \%X{spring.application.name} # optional will print spring.application.name
        
```
