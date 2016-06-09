# microservices-demo

Demo application to go with my [Microservices Blog](https://spring.io/blog/2015/07/14/microservices-with-spring) on the spring.io website.

![Demo System Schematic](https://github.com/paulc4/microservices-demo/blob/master/mini-system.jpg)

Clone it and either load into your favorite IDE or use maven directly.

## Versions

To access V1.0.0 of the repo, corresponding to Spring Cloud release-train Angel.SR6, click on the `release` tab in https://github.com/paulc4/microservices-demo.

## Using an IDE

You can run the system in your IDE by running the three servers in order: _RegistrationService_, _AccountsService_ and _WebService_.

As discussed in the Blog, open the Eureka dashboard [http://localhost:1111](http://localhost:1111) in your browser to see that the `ACCOUNTS-SERVICE` and `WEB-SERVICE` applications have registered.  Next open the Demo Home Page [http://localhost:3333](http://localhost:3333) in and click one of the demo links.

The `localhost:3333` web-site is being handled by a Spring MVC Controller in the _WebService_ application, but you should also see logging output from _AccountsService_ showing requests for Account data.

## Command Line

You may find it easier to view the different applications by running them from a command line since you can place the three windows side-by-side and watch their log output

To do this, open three CMD windows (Windows) or three Terminal windows (MacOS, Linux) and arrange so you can view them conveniently.

 1. In each window, change to the directory where you cloned the demo
 1. In the first window, build the application using `mvn clean package`
 1. In the same window run: `java -jar target/microservice-demo-1.1.0.RELEASE.jar registration` and wait for it to start up
 1. Switch to the second window and run: `java -jar target/microservice-demo-1.1.0.RELEASE.jar accounts` and again wait for
 it to start up
 1. In the third window run: `java -jar target/microservice-demo-1.1.0.RELEASE.jar web`
 1. In your favorite browser open the same two links: [http://localhost:1111](http://localhost:1111) and [http://localhost:3333](http://localhost:3333)

You should see servers being registered in the log output of the first (registration) window.
As you interact with the web-application ([http://localhost:3333](http://localhost:3333)) you should logging appear
in the second and third windows.

 1. In a new window, run up a second account-server using HTTP port 2223:
     * `java -jar target/microservice-demo-0.0.1-SNAPSHOT.jar accounts 2223`
 1. Allow it to register itself
 1. Kill the first account-server and see the web-server switch to using the new account-server - no loss of service.

