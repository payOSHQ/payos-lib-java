# Contributing

## Setting up the environment

This repository use Maven for building and dependency management. The SDK requires Java 8, but development requires JDK 17.

## Adding and running examples

You can run, modify and add new examples in [examples](./src/main/java/vn/payos/examples/) directory.

```java
// ./scr/main/java/vn/payos/examples/YourExample.java
package vn.payos.examples;

public class YourExample {
    public static void main(String[] args) {
        // ...
    }
}
```

```bash
mvn compile exec:java -Dexec.mainClass="vn.payos.examples.YourExample"
```

## Using the library from source

If you like to use the library from source, you can either [install from git](https://jitpack.io/) or link to a cloned repository.

To use a local version of this library from source in another project, you can publish it to your local Maven repository:

```bash
mvn clean install -Dgpg.skip
```

Alternative, you can build and install the JAR files directly:

```bash
mvn clean package
```

The JAR files will be available in `target` directory.

## Linting and formatting

This repository uses [Spotless](https://github.com/diffplug/spotless) with [Google Java Format](https://github.com/google/google-java-format) for code formatting.

```bash
# check formatting
mvn spotless:check

# fix all formatting automatically
mvn spotless:apply
```

## Publishing and release

### Publish with Github workflow

You can release by creating a version tag, then the [`Publish Sonatype workflow`](https://github.com/payoshq/payos-java/actions/workflows/publish-sonatype.yml) will publish the package. This requires setup secrets to be configured.

### Publish manually

Create or update `~/.m2/settings.xml` with your user token, then run:

```bash
# For development (skip GPG signing)
mvn clean install -Dgpg.skip

# For release (with GPG signing)
mvn clean deploy
```
