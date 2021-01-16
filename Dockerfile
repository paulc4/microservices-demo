FROM openjdk:8-jre
ADD target/microservices-demo-2.0.0.RELEASE.jar app.jar
EXPOSE 1111
EXPOSE 2222
EXPOSE 3333
# Optional default command
# ENTRYPOINT ["java","-jar","/app.jar","reg"]
