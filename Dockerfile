FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER xuelingkang@163.com

WORKDIR /spring-boot-demo

EXPOSE 8080

ADD spring-boot-demo.jar app.jar

CMD java -Djava.security.egd=file:/dev/./urandom -jar app.jar
