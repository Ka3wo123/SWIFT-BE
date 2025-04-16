# SWIFT APP BACKEND

This is REST API for handling Bank Identifier Codes (BIC) or SWIFT codes. It allows to retrieve SWIFT data, create new SWIFT data and delete existing ones in fast manner.

## Table of Contents

- [Stack](#stack)
- [Launching](#launching)
  - [Localhost machine](#localhost-machine)
  - [Docker containers](#docker-containers)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
  - [Unit tests](#unit-tests)
  - [Integration tests](#integration-tests)

## Stack

- Java 17
- Gradle 8.13
- Docker Compose 2.35
- Docker
- MongoDB 8.0.6

## Launching

<p style="text-align: left;">
  <img src="https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678077-computer-512.png" alt="Docker Logo" width="80"/>
</p>

### Localhost machine:

1. Clone repository `git clone https://github.com/Ka3wo123/SWIFT-BE.git`
2. Launch Mongo database in Docker container with one of the below manners or set up on your local machine:
   - use service from [docker-compose.yml](./docker-compose.yml) (`docker compose up -d swift-mongo`)
   - set up volume `docker volume create swifts-v`
     - run `docker run --name swfit-mongo -p 27017:27017 -v swifts-v -d mongo:8.0.6`
3. Start application `./gradlew bootRun --args='--spring.data.mongodb.uri=mongodb://localhost:27017/swifts'` or using configuration with environment variables in IntellijIdea in _Run/Debug Configurations_
4. Application is accessible on http://localhost:8080/v1/swift-codes/

<p style="text-align: left;">
  <img src="https://cdn4.iconfinder.com/data/icons/logos-and-brands/512/97_Docker_logo_logos-512.png" alt="Docker Logo" width="100"/>
</p>

### Docker containers

Run `docker compose up -d` in root dir.

## API endpoints

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Exception</th>
      <th>Response</th>
      <th>Body</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>GET</td>
      <td>/v1/swift-codes/{swiftCode}</td>
      <td>Get SWIFT data by specific SWIFT code</td>
      <td><a href="./src/main/java/pl/ka3wo/swift/exception/NoSwiftDataFound.java">NoSwiftDataFoundException</a></td>
      <td>Status: <code>200 OK</code><br>JSON: <a href="./src/main/java/pl/ka3wo/swift/model/dto/SwiftDataDto.java">SwiftDataDto</a></td>
      <td>-</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/v1/swift-codes/country/{countryISO2}</td>
      <td>Get SWIFT data by country ISO2 code</td>
      <td><a href="./src/main/java/pl/ka3wo/swift/exception/NoSwiftDataFound.java">NoSwiftDataFoundException</a></td>
      <td>Status: <code>200 OK</code><br>JSON: <a href="./src/main/java/pl/ka3wo/swift/model/dto/SwiftDataCountryDto.java">SwiftDataCountryDto</a></td>
      <td>-</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/v1/swift-codes</td>
      <td>Create a new SWIFT data</td>
      <td>
        <a href="./src/main/java/pl/ka3wo/swift/exception/DuplicateSwiftCodeException.java">DuplicateSwiftCodeException</a><br>
        <a href="./src/main/java/pl/ka3wo/swift/exception/RestExceptionHandler.java">MethodArgumentNotValidException</a>
      </td>
      <td>Status: <code>201 OK</code><br>JSON: <a href="./src/main/java/pl/ka3wo/swift/model/dto/CreateSwiftDataResponse.java">CreateSwiftDataResponse</a></td>
      <td>
        <pre><code>{
  "address": string,
  "bankName": string,
  "countryISO2": string,
  "countryName": string,
  "isHeadquarter": boolean,
  "swiftCode": string  
}</code></pre>
      </td>
    </tr>
    <tr>
      <td>DELETE</td>
      <td>/v1/swift-codes/{swiftCode}</td>
      <td>Delete a SWIFT data by SWIFT code</td>
      <td><a href="./src/main/java/pl/ka3wo/swift/exception/NoSwiftDataFound.java">NoSwiftDataFoundException</a></td>
      <td>Status: <code>200 OK</code><br>JSON: <a href="./src/main/java/pl/ka3wo/swift/model/dto/DeleteSwiftDataResponse.java">DeleteSwiftDataResponse</a></td>
      <td>-</td>
    </tr>
  </tbody>
</table>


## Testing

Commands to run tests:
- `./gradlew utest` - all unit tests
- `./gradlew itest` - all integration tests
- `./gradlew test` - all unit + integration tests

### Unit tests

Unit tests covers:

- service layer
  - [CSVSwiftLoaderServiceTest](./src/test/java/pl/ka3wo/swift/service/CSVSwiftLoaderServiceTest.java)
  - [SwiftServiceTest](./src/test/java/pl/ka3wo/swift/service/SwiftServiceTest.java)
- controller layer
  - [SwiftControllerTest](./src/test/java/pl/ka3wo/swift/controller/SwiftControllerTest.java)


### Integration tests

Integration tests covers:

- SWIFT data creation
  - [SwiftDataCreationIT](./src/test/java/pl/ka3wo/swift/integration/SwiftDataCreationIT.java)
- SWIFT data deletion
  - [SwiftDataDeletionIT](./src/test/java/pl/ka3wo/swift/integration/SwiftDataDeletionIT.java)
- SWIFT data retrieval
  - [SwiftDataRetrievalIT](./src/test/java/pl/ka3wo/swift/integration/SwiftDataRetrievalIT.java)
