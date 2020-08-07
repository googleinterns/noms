# Tests
This directory contains various unit tests for the JavaScript in our webapp.

## Setup

```bash
$ cd ./src/main/webapp/assets/js/tests
$ npm i
```

## Run tests

#### App engine environment (Cloud Shell)

1. Out of the top-level directory, run:
```bash
$ mvn package appengine:run
```

2. Navigate to `theurlgiven.dev/assets/js/test/feedScript.testrunner.html`