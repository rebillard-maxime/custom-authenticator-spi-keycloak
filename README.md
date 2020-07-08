##Authenticator SPI - Custom UserPasswordAuthenticator

- Run a Keycloak instance
```js
docker run --name keycloak -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin quay.io/keycloak/keycloak:10.0.2
````
- Create the .jar (using maven)
```js 
mvn clean package
```
- Deploy the jar to keycloak

```js
docker cp target/keycloak-spi-example-0.0.1-SNAPSHOT.jar keycloak:/opt/jboss/keycloak/standalone/deployments/keycloak-spi-example-0.0.1-SNAPSHOT.jar
```

Then :
1. Connect to keycloak (using admin credentials)
2. Go to Authentication section
3. Add execution (select the "browser" value)
3. Click Add Flow
4. Click Add Execution Flow
5. Select the provider created

Then you have multiple possibilities : you can
- override the authentication flow by going to the client and at the bottom click "Authentication Flow overrides", or
- bind the authentication flow directly to the "browser" (for example)
