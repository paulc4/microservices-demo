# Run Using Docker

* We'll run the microservices demo application using three docker containers;
one for the Eureka registration server and another two, each for the micro services.

## Table of contents

* [Method 1: jib:dockerBuild](#method-1)
    * [Build the Image with Jib Maven Plugin](#build-the-image-with-jib-maven-plugin)
    * [Prepare a docker bridge type network](#prepare-a-docker-bridge-type-network)
    * [Run the Eureka Registration Server](#run-the-eureka-registration-server)
        * [Finding the IP Address of a docker container](#finding-the-ip-address-of-a-docker-container)
    * [Run the Accounts Server](#run-the-accounts-server)
    * [Run the Web Server](#run-the-web-server)
    * [Using the application](#using-the-application)
* [Method 2: jib:build](#method-2)
    * [Using Jib build goal to push an image to a registry](#using-jib-build-goal-to-push-an-image-to-a-registry)
        * [Supplying Repository Credentials to JIB](#supplying-repository-credentials-to-jib)
        * [Pushing an image to a repository](#pushing-an-image-to-a-repository)
    * [Pulling an image from a repository](#pulling-an-image-from-a-repository)
    * [Running an image from a repository](#running-an-image-from-a-repository)
* [Docker cleanup](#docker-cleanup)


* First, we'll use the `jib maven plugin` to build the docker image for us.
There's no need for a `Dockerfile` since plugin builds optimized Docker and OCI images
for the Java applications without a Docker daemon and without deep mastery of
Docker best-practices.


* Jib is included in this project as a maven plugin. We'll be calling maven Jib
goals manually from command line. With the following [Method-1](#method-1), we'll
be using Jib's `mvn jib:dockerBuild` command to build a docker image.


* With the later [Method-2](#method-2) we'll be pulling a pre-built image from my
public [DockerHub](https://registry.hub.docker.com/ "DockerHub") repository.

# Method 1

## Build the Image with Jib Maven Plugin

* Jib is configured in the `plugins/jib-maven-plugin` section of the `pom.xml`. The following command
will build a docker image named `microservices-demo`:

>
```
mvn clean package jib:dockerBuild -DskipTests
```

* Before we run the image, we'll create a network bridge for the microservices to communicate with each other
internally in the docker container.

## Prepare a docker bridge type network

* The dockerized microservices need to communicate with each other inside the docker daemon container. The
container's networking is not to be confused with localhost's, the machine which hosts docker. Inside a
docker container 'localhost' has a different IP Address than our hosting machine. We're going to create a
bridge type docker network:

>
```
docker network create accounts-net
```

* Now we can run the first of three containers:


## Run the Eureka Registration Server


* We'll run the `microservices-demo` container three times from three different CMD/Terminal windows
and each time we'll be running the Java application in a different mode. In practice we'll be running Paul's `io.pivotal.microservices.services.Main` java entry point class, each time with a different argument
indicating what type of server to invoke.


* We'll alias the first container `reg-server`. The following command runs the Eureka
registration server:

>
```
docker run --name=reg-server --network accounts-net -p 8761:8761 tnsilver/microservices-demo reg
```

* **Note** the first response line to this command. It contains an IP Address. It should say
something like: `Running on IP: 172.18.0.2`. (your IP might be different and that's fine).


* Make a note of this IP Address. You will need it for later.

### Finding the IP Address of a docker container

* In case you've missed it, or it wasn't shown, there's other ways to find out the IP address
of the `reg-server` host:


* The easiest way is to browse to the Eureka dashboard at <http://localhost:8761/>, to login with
`admin/password` and to scroll down to the `Instance Info` section. The IP address should be
listed under `IpAddr`.


* Another (rather verbose) way is to get the IP Address from docker by inspection of the container's environment,
which is just a long over-complicated `json` string. The following command will list the IP address for each
running container. `reg-server` should be there too:

>
```
docker inspect -f '{{.Name}} - {{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $(docker ps -aq)
```

* Finally, if you login to the container's bash shell, you could find the IP Address in the container's
`/etc/hosts` file:

>
```
docker exec reg-server cat /etc/hosts
```

## Run the Accounts Server

* In a new CMD/Terminal window, run a second `microservices-demo` container for the accounts micro service.


* This time we'll alias the container `accounts-server` and we'll use the `-e` flag to pass the
`EUREKA_URL` environment variable to docker.


* The `EUREKA_URL` environment variable is defined in the `accounts-server.properties` file as the first
argument to the value of the `eureka.client.service-url.defaultZone` property. When the environment variable
is empty, spring picks up whatever the default is after the `:` colon separator. This is how the application
behaves when we run it from the IDE or command line.


* With docker, we'll be passing a value for the `EUREKA_URL` variable into the container's environment.


* **Before you run** the `accounts-server` with the IP Address of the Eureka registration server host - make sure
to replace the IP Address part of the following command with the IP Address you've obtained earlier:

>
```
docker run --name=accounts-server --network accounts-net -e "EUREKA_URL=http://client:password@172.18.0.2:8761/eureka" -p 7070:7070 tnsilver/microservices-demo accounts
```

* See the [how to remove a failed container](#how-to-remove-a-failed-container) section in case there too many stack trace messages showing up in the console/terminal after execution of the last run command.


* Return to the Eureka Dashboard in your browser and refresh the screen. You should see that `ACCOUNTS-SERVICE`
is now registered.

## Run the Web Server

* Finally, in a new CMD/Terminal window, run a third container for the accounts `web-service`. This is the web
application for viewing account information. It performs by requesting accounts info from the `accounts-server`
microservice:


* Again - **replace the IP Address** in the command with the value you've obtained earlier. We'll alias the
container `web-server`:

>
```
docker run --name=web-server --network accounts-net -e "EUREKA_URL=http://client:password@172.18.0.2:8761/eureka" -p 8080:8080 tnsilver/microservices-demo web
```

## Using the application

* Return to the Eureka Dashboard in your browser and refresh the screen. You should see that `ACCOUNTS-SERVICE`
and `WEB-SERVICE` are now registered.


* In a second browser tab, go to <http://localhost:8080>. This is the web interface you've just deployed and you
should be able to view, list and search for account info.


* Congrats! You have dockerized the application.

### How to remove a failed container

* In case you've mistyped a command or forgot to replace the IP Address in the run command, and
you're getting a whole bunch of ugly `com.netflix.discovery.shared.transport.TransportException`
stack traces - hit `CRTL+C`.


* List the docker deployed containers by using the command `docker ps -a` and take note of the
problematic container alias (under `Names`) and it's `Container ID`.


* The alias that you've used in the run command will appear under the `Names`  column and the
Container ID is by the default format of the `ps` command, the left most column.


* Now kill it with `docker container rm [CONTAINER-ID] -f` and issue the correct run command with
the correct IP Address.

# Method 2:

### Using Jib build goal to push an image to a registry

* You don't need to follow this section. This is how I pushed the image to my public
registry, so that you can pull it.


* It starts with building the image like before, but pushing it to a registry instead
of keeping it in the maven project `target` directory:

>    * mvn clean package jib:build -DskipTests

* My `DockerHub` repository is public. You won't need to authenticate in the next section - but sooner than later
you're going to have to use credentials to login with Docker. It important to know how to supply credentials
securely to jib.

#### Supplying Repository Credentials to Jib

*  For the next `jib:build` goal, the easiest way is to provide credentials through maven in `~/.m2/settings.xml`.
(I'm assuming the use of a [DockerHub](https://hub.docker.com/ "DockerHub") repository in this example):

>
```
<servers>
    <server>
        <id>registry.hub.docker.com</id>
        <username><DockerHub Username></username>
        <password><DockerHub Password></password>
    </server>
</servers>
```

* Note however: The recommended way by Google to provide the credentials is
to use helper tools, which can store the credentials in an encrypted format in
the file system.


* We can use `docker-credential-helpers` instead of storing plain-text credentials in `settings.xml`, which is much
safer. See for example [docker-credential-helpers](https://github.com/docker/docker-credential-helpers "docker-credential-helpers").


* The process of installing the `docker-credential-helpers` and configuring an encrypted password for Jib may
get tedious and cumbersome. [Geoff Hudik](https://geoffhudik.com/tech/author/thnk2win/ "Geoff Hudik") had made a
shell script for installing and configuring [docker pass credential helper on Ubuntu](https://geoffhudik.com/tech/2020/09/15/docker-pass-credential-helper-on-ubuntu/ "docker pass credential helper on Ubuntu"), which simplifies the task.


* The `docker-credentials.sh` script is included in the `docker` directory under the root of this project.
You can run it as `root` using `sudo` and it will painfully perform the installations. It will prompt you
for input when needed.

#### Pushing an image to a repository

* To push an image to a repository, configure the jib maven plugin `image`
element inside the `to` destination element, with the repository url prefix and
user. For example:

>
```
<plugin>
	<groupId>com.google.cloud.tools</groupId>
	<artifactId>jib-maven-plugin</artifactId>
	<configuration>
	    <from>
	    	<image>openjdk:16.0.1-jdk-slim@sha256:e20175812fb1b559a3a98d07515c42c030531d9b06d65821d69968e048c752a1</image>
	    </from>
		<to>
			<image>registry.hub.docker.com/tnsilver/${project.artifactId}</image>
			<!-- <image>tnsilver/${project.artifactId}</image> -->
		</to>
		<container>
		    <creationTime>${build.timestamp}</creationTime>
			<ports>
			    <port>8761</port>
			    <port>7070</port>
				<port>8080</port>
			</ports>
		</container>
	</configuration>
</plugin>
```

* To push the image to `registry.hub.docker.com` all we have to do other than wait (this can take some time):

>    * mvn clean package jib:build -DskipTests

* If the process gets stuck for network latency or Jib had lost a connection
with the repository, all you have to do is `mvn jib:build` and it'll pick up from
where it had left. Uploading those hundreds of megabytes can take some time.


* There's no docker cleanup to perform! We don't even need the docker daemon installed.
Once the image is saved to the repository, anyone (authorized) can install the image on a
docker daemon anywhere. It is currently a public repository in my DockeHub so no authorization
is required other than logging in with docker (`docker login`).

### Pulling an image from a repository

* If you had previously installed a microservices-demo on your docker, clean it up.
see [Docker cleanup](#docker-cleanup)

* The basic pull command (no need to actually do it - see next section) is:

>
```
docker pull tnsilver/microservices-demo:latest
```

#### Running an image from a repository

* We don't have to explicitly pull a DockerHub image. Once we tell docker to run
the `tnsilver/microservices-demo` image and it won't be found locally, docker will attempt
to find it in the DockerHub repository. So basically, you can run the image now.
(see [Prepare a docker bridge type network](#prepare-a-docker-bridge-type-network) and follow from there).

>
```
docker network create accounts-net
```

* and:

>
```
docker run --name=reg-server --network accounts-net -p 8761:8761 tnsilver/microservices-demo reg
```

* and in the next window

>
```
docker run --name=accounts-server --network accounts-net -e "EUREKA_URL=http://client:password@172.18.0.2:8761/eureka" -p 7070:7070 tnsilver/microservices-demo accounts
```

* and then in the next window

>
```
docker run --name=web-server --network accounts-net -e "EUREKA_URL=http://client:password@172.18.0.2:8761/eureka" -p 8080:8080 tnsilver/microservices-demo web
```

## Docker cleanup


* Whenever you're done, failed, rebooted or just want to clean up docker from the
`tnsilver/microservices-demo` image and various containers, run the following commands.


*  To remove all the `tnsilver/microservices-demo` containers:

>
```
docker container rm $(docker ps -a -q --filter ancestor=tnsilver/microservices-demo) -f
```

*  To remove all the `microservices-demo` images:

>
```
docker rmi tnsilver/microservices-demo -f
```

*  To remove all dangling (left over) images (if any exist):

>
```
docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)
```

* ...and finally, to remove the `accounts-net` network:

>
```
docker network rm accounts-net
```
