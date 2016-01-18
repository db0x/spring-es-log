# spring-es-log
Integration of ElasticSearch-Log Appender in spring-boot without xml configuration

[![Build Status](https://travis-ci.org/db0x/spring-es-log.svg?branch=master)](https://travis-ci.org/db0x/spring-es-log)

bases on https://github.com/internetitem/logback-elasticsearch-appender

motivation : use elasticsearch-appender without logback.xml 
cofiguration in appication.yml of the spring boot container


application.yml exmpl:

```yml
server:
  port: 8080                                       # is required if you use parameters.port
   
spring:
  application:
    name: myApp                                    # is required if you use parameters.application
    log: 
      enable-es-log: true                          # default is true
      host: localhost                              # host of the es cluster default is localhost
      ports: [9200,9300]                           # ports of the es cluster default is 9200 / 9300 (HTTP / transport)
      type: "eslog"                                # _type in index will be used for clean-query  
      index-name: "log-%date{yyyy-MM-dd}"          # pattern for index-name default is log-%date{yyyy-MM-dd}
      clean: 5                                     # indices will be cleaned after x days (null -> never clean indices) default is 5 
      parameters:        
        severity: "%level"                         # default is %level
        thread: "%thread"                          # default is %thread
        logger: "%logger"                          # default is %logger
        stacktrace: "%ex"                          # default is %ex
        host: "%X{host}"                           # optional will print name of the host
        port: "%X{port}"                           # optional will print server.port if set
        application: "%X{spring.application.name}" # optional will print spring.application.name
        
```
Usage
=====
simply add it to your maven pom 
```xml
		<dependency>
			<groupId>de.db0x</groupId>
			<artifactId>spring-es-log</artifactId>
			<version>0.0.1</version>
		</dependency>
```
and annotate an class with @EnableESLog.

* pro + no xml configuration is needed
* neg - the first events are not logged to ES because the appender is armed after the spring-context is loaded

Usage of additional %X{...} parameters
===================================
you can add new columns to the index using parameter and %X{...}

with %X you can read what was put into org.slf4j.MDC
