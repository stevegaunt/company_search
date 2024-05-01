# Company Search Spring Excerse

> ### **Java 21 + Spring Boot 3**.




## How it works
The example project aims to represent a simple customer 
search proxing a external search apis to aggregate and reponses



### Project structures
The project is implemented based on Java 21 and Spring Boot 3,
utilizing various Spring technologies such as Spring MVC, Spring Security
and JUnit5 / Wiremock for writing test codes.

Authentication is done using x-api-key as part of the header.  This
is the checked with an env value to validate against, and then passed onto 
the underlyng search apis.  Ideally these would be stored in some sort of encrypted
safe, such as AWS secrets manager.

Virtual threads have been enables to ideally increase scalability with
enabling lighter resource consumption and enhanced concurrency. It's possible
multiple apis calls are need in parallel for aggrated the results. 
Although this is enables and setup to be asyncronous, defintely need more digging to see
this is truly running parallel

The next step is to chache the results of the custer searchs into some persisence
or Cache (eg, nosql database   ) given the search results are varied

#### Classes
- ~Controller: Processes HTTP requests, calls business logic, and generates responses.
- ~Service: Implements business logic and aggregates the reponses from third party search APIs.
- ~Repository: Not yet added, but owuld be caching the results from above in a 
  NOSQL or redis cache..

---

## Getting started

> **Note:**  JDK 21 installed.

### Run application

```shell
./gradlew :bootstrap:bootRun
```

### Apply code style

> **Note:** When you run the `build` task, this task runs automatically. If the code style doesn't match, the build will fail.

```shell
./gradlew spotlessApply
```

### Run test

```shell
./gradlew test
```

### Run build

```shell
./gradlew build
```

### Run application

```shell
API_KEY=[REAL_API-KEY] ./gradlew :bootRun 

```
### example usage

```shell
curl --location 'http://localhost:8080/api/company/search?activeCompaniesOnly=false' \
--header 'Content-Type: application/json' \
--header 'x-api-key: your api key' \
--data '{
"companyName" : "test company",
"companyNumber" : "test number"
}'

```
