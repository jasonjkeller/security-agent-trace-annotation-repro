# Security Agent Trace Annotation Repro

## Problem

The presence of the `security` stanza in the `newrelic.yml` causes the `@Trace(dispatcher=true)` annotation to fail to start a `Transaction`, even with all the `security` features disabled in the yaml (see config example below). This is evidenced by the `com.newrelic.agent.bridge.NoOpTransaction@xxxxxx` objects returned in this scenario.

```yaml
  security:
    enabled: false
    mode: IAST
    validator_service_url: wss://csec.nr-data.net
    agent:
      enabled: false
    detection:
      rci:
        enabled: false
      rxss:
        enabled: false
      deserialization:
        enabled: false
```

If you comment out the entire `security` config stanza then usage of the `@Trace(dispatcher=true)` annotation properly starts a `Transaction`, as evidenced by the `com.newrelic.agent.TransactionApiImpl@xxxxxx` objects returned.

Interestingly, adding the `security` config via system property instead of yaml seems to work properly.

```
"-Dnewrelic.config.security.enabled.false",
"-Dnewrelic.config.security.mode=IAST",
"-Dnewrelic.config.security.validator_service_url=wss://csec.nr-data.net",
"-Dnewrelic.config.security.agent.enabled=false",
"-Dnewrelic.config.security.detection.rci.enabled=false",
"-Dnewrelic.config.security.detection.rxss.enabled=false",
"-Dnewrelic.config.security.detection.deserialization.enabled=false",
```

Environment variables also work properly.

```
NEW_RELIC_CONFIG_SECURITY_ENABLED=FALSE
NEW_RELIC_CONFIG_SECURITY_MODE=IAST
NEW_RELIC_CONFIG_SECURITY_VALIDATOR_SERVICE_URL=wss://csec.nr-data.net
NEW_RELIC_CONFIG_SECURITY_AGENT_ENABLED=FALSE
NEW_RELIC_CONFIG_SECURITY_DETECTION_RCI_ENABLED=FALSE
NEW_RELIC_CONFIG_SECURITY_DETECTION_RXSS_ENABLED=FALSE
NEW_RELIC_CONFIG_SECURITY_DETECTION_DESERIALIZATION_ENABLED=FALSE
```

## Run the Repro

### Configuration

In [build.gradle.kts](build.gradle.kts) you must replace `NR_LICENSE_KEY` with a valid New Relic license key: `-Dnewrelic.config.license_key=NR_LICENSE_KEY`

The project includes two agent yaml config files, one with the `security` stanza and one without it, to illustrate the non-working and working cases, respectively.  
* [newrelic-with-security-config.yml](newrelic%2Fnewrelic-with-security-config.yml)
* [newrelic-without-security-config.yml](newrelic%2Fnewrelic-without-security-config.yml)

### Case 1: With Security Config (No Transactions Created)

In [build.gradle.kts](build.gradle.kts) set `-Dnewrelic.config.file=./newrelic/newrelic-with-security-config.yml` to use the config file that includes the `security` stanza.

Execute `./gradlew run` from the project directory to run the repro app.

In this case the repro app will log that the transaction created by the `@Trace(dispatcher=true)` annotation is a `NoOpTransaction`.

```
Transaction (startNewRelicTransaction): com.newrelic.agent.bridge.NoOpTransaction@530b1267
Transaction (childTrace): com.newrelic.agent.bridge.NoOpTransaction@530b1267
```

### Case 2: Without Security Config (Transactions Created)

In [build.gradle.kts](build.gradle.kts) set `-Dnewrelic.config.file=./newrelic/newrelic-without-security-config.yml` to use the config file without the `security` stanza.

Execute `./gradlew run` from the project directory to run the repro app.

In this case the repro app will log that the transaction created by the `@Trace(dispatcher=true)` annotation is a `TransactionApiImpl`.

```
Transaction (startNewRelicTransaction): com.newrelic.agent.TransactionApiImpl@faf4600
Transaction (childTrace): com.newrelic.agent.TransactionApiImpl@faf4600
```

