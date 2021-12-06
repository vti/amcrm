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

### Local environment

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
      ```
   3. Environment variables
   
      ```bash
      AMCRM_PORT=1234
      AMCRM_STORAGE_PROVIDER=database
      AMCRM_STORAGE_DATABASE_OPTIONS=database=db.db
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

   ```bash
   $ docker build . -t amcrm
   ```
2. Run container

   ```bash
   $ docker run --rm amcrm -v $PWD:
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

Domain holds the domain logic of the application without relying on a specific storage or a framework. Domain enforces its own interfaces for different purposes that are implemented based on the need.

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