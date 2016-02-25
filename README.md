# microservices-demo

Demo application to go with my [Microservices Blog](https://spring.io/admin/blog/2181-microservices-with-spring) on the spring.io website.

![Demo System Schematic](https://github.com/paulc4/microservices-demo/blob/master/mini-system.jpg)

Clone it and either load into your favorite IDE or use maven directly.

## Using an IDE

You can run the system in your IDE by running the three servers in order: _RegistrationService_, _AccountsService_ and _WebService_.

As discussed in the Blog, open the Eureka dashboard [http://localhost:1111](http://localhost:1111) in your browser to see that the `ACCOUNTS-SERVICE` and `WEB-SERVICE` applications have registered.  Next open the Demo Home Page [http://localhost:3333](http://localhost:3333) in and click one of the demo links.

The `localhost:3333` web-site is being handled by a Spring MVC Controller in the _WebService_ application, but you should also see logging output from _AccountsService_ showing requests for Account data.

## Command Line

You may find it easier to view the different applications by running them from a command line since you can place the three windows side-by-side and watch their log output

To do this, open three CMD windows (Windows) or three Terminal windows (MacOS, Linux) and arrange so you can view them conveniently.

 1. In each window, change to the directory where you cloned the demo and then to the folder containing the relevant service.
 1. In the first window, build the application using `mvn clean package`
 1. In the same window run: `spring-boot:run`
 1. Switch to the second window and run: `spring-boot:run`
 1. In the third window run: `spring-boot:run`
 1. In your favorite browser open the same two links: [http://localhost:1111](http://localhost:1111) and [http://localhost:3333](http://localhost:3333)

You should see servers being registered in the log output of the first (registration) window.
As you interact you should logging in the second and third windows.

## Using Docker
You may want to run the services in Docker containers. The following instructions will help you run the demo using three containers on a single host:
To use this instructions you should have docker installed and a host running the Docker deamon.

- change directory to the root directory of the project
- run `mvn clean package`

Step 1 - build the docker images:
- to create the image for the discovery service run: `docker build -t spring-cloud-demo-discovery discovery-service`
- to create the image for the account service run: `docker build -t spring-cloud-demo-account account-service`
- to create the image for the web service run: `docker build -t spring-cloud-demo-web web-service`

Step 2 - create a network for the containers
- run: `docker network create spring-cloud-demo`

Step 3 - run the containers:
In 3 different terminal windows run the following commands
- to run the container for the discovery service run: `docker run -it --net=spring-cloud-demo -p 1111:1111 --name discovery --hostname=discovery spring-cloud-demo-discovery`
- to run the container for the account service run: `docker run -it --net=spring-cloud-demo --name account --hostname=account spring-cloud-demo-account`
- to run the container for the web service run: `docker run -it --net=spring-cloud-demo -p 80:3333 --name web --hostname=web spring-cloud-demo-web`

Step 4 - use the application using your browser:
- find the ip of the host machine. to do so type `docker-machine ls` and find the ip of the host you are using.
- browse [http://your-host-ip](http://your-host-ip) to access the web service
- browse [http://your-host-ip:1111](http://your-host-ip:1111) to access the discovery service console

Step 5 - run another account service and check the load balancing and failover:
- to run the container for the account service run: `docker run -it --net=spring-cloud-demo --name account-2 --hostname=account-2 spring-cloud-demo-account`
- browse [http://your-host-ip](http://your-host-ip) to access the web service
- browse [http://your-host-ip:1111](http://your-host-ip:1111) to access the discovery service console

