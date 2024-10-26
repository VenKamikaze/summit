#!/bin/bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -agentlib:jdwp=transport=dt_socket,address=localhost:8000,server=y,suspend=n"
