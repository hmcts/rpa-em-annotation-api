# Evidence Management Annotation App
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/hmcts/em-annotation-app.svg?branch=master)](https://travis-ci.org/hmcts/em-annotation-app)
[![codecov](https://codecov.io/gh/hmcts/em-annotation-app/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/em-annotation-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8a50dd2a7b9144029e8547bf019fe2c7)](https://www.codacy.com/app/HMCTS/em-annotation-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8a50dd2a7b9144029e8547bf019fe2c7)](https://www.codacy.com/app/HMCTS/em-annotation-app)
[![Known Vulnerabilities](https://snyk.io/test/github/hmcts/em-annotation-app/badge.svg)](https://snyk.io/test/github/hmcts/em-annotation-app)

Annotation API is a backend service to store and retrieve annotations.

## Quickstart
```bash
#Cloning repo and running though docker
git clone https://github.com/hmcts/em-annotation-app.git
cd em-annotation-app/
az acr login --name hmcts --subscription 1c4f0704-a29e-403d-b719-b90c34ef14c9
docker-compose -f docker-compose-dependencies.yml up
```

### Swagger UI
To view our REST API go to {HOST}:{PORT}/swagger-ui.html
> http://localhost:8080/swagger-ui.html

### API Endpoints
A list of our endpoints can be found here
> https://hmcts.github.io/reform-api-docs/specs/rpa-em-annotation-app.json

### Tech

It uses:

* Java8
* Spring boot
* Junit, Mockito and SpringBootTest and Powermockito
* Gradle
* [lombok project](https://projectlombok.org/) - Lombok project

### Plugins
* [lombok plugin](https://plugins.jetbrains.com/idea/plugin/6317-lombok-plugin) - Lombok IDEA plugin

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
