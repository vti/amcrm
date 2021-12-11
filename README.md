# AMCRM

AMCRM is a backend implementation for customer management. It has a public REST api that is documented below.

## REST API

### Overview

Application has en embedded playground/documentation accessible at http://localhost:4567/docs by default.

### Errors

Upon an error the backend returns an error object with the details when possible.

A typical error has the following structure:

| Field    | Type          | Description                               |
|----------|---------------|-------------------------------------------|
| messsage | string        | Human readable error message              |
| details  | array[string] | A list of messages providing more details |

Generic error example:

```http
HTTP/1.1 500 System error
Content-Type: application/json

{
  "message": "Internal system error"
}
```

Validation error example:

```http
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/json

{
  "details": [
    "$.id: is missing but it is required",
    "$.name: is missing but it is required",
    "$.surname: is missing but it is required"
  ],
  "message": "Validation failed"
}
```

### Authentication

Authentication is session-based, once you have a session id you can perform requests to the restricted endpoints. Since
Users resource is reserved only for admin, every user has a role: anonymous, user or admin. Based on the role the access
is restricted.

Session expires in 1h but is automatically prolonged when used.

Session is sent in the `Authorization` header:

```http
GET /customers
Authorization: e059e8e2-585e-11ec-85f1-7f102f05f20c
...
```

#### OAuth

To get a session id one can use a GitHub public oauth. An application needs to be created and client_id and
client_secret configured.

1. Get the login url

```http
POST /oauth/github
```

```http
HTTP/1.1 200 OK
{
  "location": "https://github.com/login/oauth/authorize?client_id=1234567890"
}
```

2. After the user comes back, the authentication in successful and their name is recognized a new session id is created
   and returned.

```http

{
  "sessionId":"cc0b9dea-585e-11ec-a20f-bf59e60f2403"
}
```

Initially the users can be created by the default admin that is created automatically during the start of application if
there are no users.

### Pagination

It is possible to pass a limit and an offset for paginating the list results, which is not the best choice of course,
something like a proper key set would be much more efficient, but it can be easily change when the need arises.

```http
GET /customers?limit=100&page=2
```

```http
HTTP/1.1 200 OK
Link: <http://localhost:4567?limit=100&page=1>; rel="prev", <http://localhost:4567?limit=100&page=3>; rel="next"

```

### Resources

#### Customers

*ListCustomers*

```http
GET /customers
```

*CreateCustomer*

```http
POST /customers
```

*GetCustomerDetails*

```http
GET /customers/{customerId}
```

*PatchCustomer*

```http
PATCH /customers/{customerId}
```

*DeleteCustomer*

```http
DELETE /customers/{customerId}
```

#### Users

*ListUsers*

```http
GET /users
```

*CreateUser*

```http
POST /users
```

*GetUserDetails*

```http
GET /users/{userId}
```

*ToggleUserAdminStatus*

```http
POST /users/{userId}/admin
```

*DeleteUser*

```http
DELETE /users/{userId}
```

## Compiling & Running application

Build & Runtime requirements:

- JRE/JDK > 8
- SQLite database (optional)
- Docker (for Docker environments)

### Storage

Application supports two storages:

- in-memory (not thread-safe, primarily for testing)
- database (transactional optimistic locking)

### Photo storage

There is a default local filesystem photo storage included, but it can be easily extended to use some external file
storage like AWS S3 or similar. In case of a local storage the path is saved in the domain storage and the base url is
automatically prepended during the runtime.

By default, photos are saved into the `public/` directory.

### Local environment

There is a [maven wrapper](https://maven.apache.org/wrapper/) available, so you don't have to rely on a system maven to
be present or to have a specific version. Instead of running `mvn` run `./mvnw`.

1. Build

    ```bash
    $ ./mvnw clean package
    ```
2. Configure

   Configuration can be mixed, for example environment variables overwrite config file, which overwrites default values.

   1. Defaults
      1. port: 4567
      2. storage provider: memory
   2. Config file (YAML format)

       ```yml
      port: 1234
      storage:
        provider: database
        options:
          database: db.db
      oauth:
        client_id: "1234567890"
        client_secret: 12039d6dd9a7e27622301e935b6eefc78846802e
      ```
   3. Environment variables
   
      ```bash
      AMCRM_PORT=1234
      AMCRM_STORAGE_PROVIDER=database
      AMCRM_STORAGE_DATABASE_OPTIONS=database=db.db
      AMCRM_OAUTH_CLIENT_ID=1234567890
      AMCRM_OAUTH_CLIENT_SECRET=12039d6dd9a7e27622301e935b6eefc78846802e
      ```

4. OPTIONAL. Setup database (when using database storage)

    ```bash
    $ sqlite3 db.db < src/main/resources/db.sql
    ```
5. Run

   1. Using a bash wrapper
    ```bash
    $ bin/amcrm --config config.yml --port 4567
    # or
    $ bin/amcrm --port 4567
    # or
    $ AMCRM_PORT=1234 bin/amcrm
    ```

   2. Directly (it's an über-jar)
   ```bash
   $ java -jar target/amcrm-1.0-SNAPSHOT.jar ...
   ```

### Docker environment

1. Build image

Building an image is a multistage process that
utilizes [buildkit](https://docs.docker.com/develop/develop-images/build_enhancements/) and maven repository caching to
speed things up.

   ```bash
   $ bash build-docker.sh
   # or
   $ DOCKER_BUILDKIT=1 docker build . -t amcrm
   ```
4. Run container

   The `public` directory is where the customers' photos will be uploaded. When not mapped they will be gone when the
   container stops.

   ```bash
   $ docker run --rm -p 4567:4567 -v /opt/amcrm/public:$PWD/public amcrm
   ```

## Contributing

### Code Style & Formatting

Make sure before submitting a PR the code is properly formatted. This is done automatically by running the following
command (or install a git hook `git config core.hooksPath .githooks`):

```bash
$ ./mvnw spotless:apply
```

### Generating database schema code

Every time the database is changed we need to regenerate the code from the database:

```bash
$ sqlite3 db.db < src/main/resources/db.sql
$ mvn -P jooq package -DjooqDatabase=db.db
```

### Building & Testing

To build and run all the test suite run the following command:

```bash
$ ./mvnw clean package
```

Tests are broken down into:

- unit tests (mind the quotes)
   ```bash
   $ ./mvnw test -D'groups=!integration,!functional'
   ```
- integration tests (tagged as "integration")
   ```bash
   $ ./mvnw test -Dgroups=integration
   ```
- functional tests (tagged as "functional")
   ```bash
   $ ./mvnw test -Dgroups=functional
   ```

## Code Structure & Reasoning

The implementation follows Clean Architecture / Domain Driven Design approach. Thus, application is split into the
following parts:

- domain
- infra
- api

### Domain

Domain holds the domain logic of the application without relying on a specific storage or a framework. Domain enforces
its own interfaces for different purposes that are implemented based on the need.

Domain contains of Entities, Events, Values Objects, Repositories & Commands.

- Entities: identifiable objects that implement business behavior.
- Events: events that happened in the domain (e.g. UserCreated).
- Value Objects: immutable structures.
- Repositories: storage abstractions.
- Commands: actions performed on domain objects to implement the business logic.

### Infrastructure

Infrastructure implements domain interfaces, provides specific helper classes like factories.

In addition, infrastructure holds Views that are the optimized read-only views for the storages. They can be used for
viewing the domain objects through some mapping classes, building reports etc.

### API

API is an adaptor that plugs into the domain and exposes different actions through REST API.

### Services

Resources are organized as Services with appropriate method mapping (e.g. service `CustomerService`
with `listCutomers()` is mapped to `GET /customers`).

#### Validation

Input validation is done by using JSON schemas.

#### Data Mapping

Data from domain to the users is mapped by using DTOs (e.g. CustomerSummary).

## Notable Dependencies, Frameworks & Libraries

[jooq](https://www.jooq.org/) — is a thin abstraction over SQL that by using code generations guarantees type safety (
e.g. typos in the column names, wrong type mapping etc.).

[armeria](https://armeria.dev) — is a Netty-based microservice framework that in addition to REST-like services allows
building RPCs systems (Thrift, gRPC). It is event-driven, supports different metrics & tracing collection and provides a
useful documentation/playground service out of the box.

[jcommander](https://jcommander.org/) — simplifies command line argument parsing.

[rest-assured](https://rest-assured.io/) — DSL for building functional API tests.

[jackson](https://github.com/FasterXML/jackson) — swiss-army knife for JSON/YAML processing.