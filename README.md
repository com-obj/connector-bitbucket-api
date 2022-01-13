# Connector for Bitbucket Cloud API
This is midPoint/ConnId connector for Bitbucket Cloud API. It is meant for managing users in Bitbucket Cloud.

## DISCLAIMER
**This connector was created and tested only for Bitbucket Cloud.**

## Build with Maven
Clone this repository and run:
```
mvn clean package
```
Now you can find jar file on path: target/connector-bitbucket-0.0.1-SNAPSHOT.jar

## Instalation
Copy connector-bitbucket-0.0.1-SNAPSHOT.jar to connector-bitbucket-0.0.1-SNAPSHOT.jar and restart midpoint.

## Config
Create **App password** Recommended approach is using Bitbucket [documentation](https://support.atlassian.com/bitbucket-cloud/docs/app-passwords/). 

Now you should be able setup connection. Use your **bitbucket login** as *username* and your **App password** as *password*.