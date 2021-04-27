#!/bin/bash

mvn clean package
java --module-path javafx/16/macos/lib --add-modules javafx.controls,javafx.fxml -jar target/aws-transcribe-sample-application-1.0-SNAPSHOT.jar
