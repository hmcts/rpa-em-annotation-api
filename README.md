# Evidence Management Annotation App
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/hmcts/em-annotation-app.svg?branch=master)](https://travis-ci.org/hmcts/em-annotation-app)
[![codecov](https://codecov.io/gh/hmcts/em-annotation-app/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/em-annotation-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8a50dd2a7b9144029e8547bf019fe2c7)](https://www.codacy.com/app/HMCTS/em-annotation-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8a50dd2a7b9144029e8547bf019fe2c7)](https://www.codacy.com/app/HMCTS/em-annotation-app)
[![Known Vulnerabilities](https://snyk.io/test/github/hmcts/em-annotation-app/badge.svg)](https://snyk.io/test/github/hmcts/em-annotation-app)

Annotation API is a backend service to store and retrieve annotations.

## Quickstart.
To pull all dependencies and set up IDAM data run:
```bash
#Cloning repo and running though docker
git clone https://github.com/hmcts/em-annotation-app.git
cd em-annotation-app/
az acr login --name hmctspublic && az acr login --name hmctsprivate

Refer to the Developer Environments page on Confluence to get the  start-local-environment.sh & docker-compose-dependencies.yml files.

docker-compose -f docker-compose-dependencies.yml pull
./bin/start-local-environment.sh <DOCMOSIS_ACCESS_KEY_VALUE>
```

Run below command to setup the db:
```
./gradlew migratePostgresDatabase
```

Run the below to start the application:
```
./gradlew bootRun
```

### Swagger UI
To view our REST API go to {HOST}:{PORT}/swagger-ui.html
> http://localhost:8080/swagger-ui.html

### API Endpoints
A list of our endpoints can be found here
> https://hmcts.github.io/reform-api-docs/specs/rpa-em-annotation-app.json

### Running contract or pact tests:

You can run contract or pact tests as follows:
```
./gradlew clean
```

```
./gradlew contract
```

You can then publish your pact tests locally by first running the pact docker-compose:

```
docker-compose -f docker-pactbroker-compose.yml up
```


and then using it to publish your tests:

```
./gradlew pactPublish
```

### Tech

It uses:

* Java11
* Spring boot
* Junit, Mockito and SpringBootTest and Powermockito
* Gradle
* [lombok project](https://projectlombok.org/) - Lombok project

### Plugins
* [lombok plugin](https://plugins.jetbrains.com/idea/plugin/6317-lombok-plugin) - Lombok IDEA plugin

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

