##Regiter an application in twitter

Check the image in images folder on how to create an app in Twitter

Go to https://apps.twitter.com/
Click on Keys and Access Token
Copy Consumer Key (API Key) as app-id
Consumer Secret (API Secret) as app-secret


##Set the app-id and app-secret in application.properties from the above copied values

spring.social.twitter.app-id=
spring.social.twitter.app-secret=


##To run application using mvn

```
./mvnw spring-boot:run
```



##Example of REST API Operations

http://localhost:8080/svc/v1/tweets/gethashtags/%23manutd


##twitter4jclient

twitter4jclient works if you provide keys in the twitter4jclient class
