> an exploration of different options to build JavaFX applications into native images

[![.github/workflows/native.yaml](https://github.com/Til7701/javafx-native-image-sample/actions/workflows/native.yaml/badge.svg)](https://github.com/Til7701/javafx-native-image-sample/actions/workflows/native.yaml)

# Known TODOs

- `XSetErrorHandler() called with a GDK error trap pushed. Don't do that.` when running on linux
- (maybe remove graalvm usage?)

# Native Package Example for JavaFX Application

By default, the sample app is built with jpackage, but it can also be built into a fat jar and further into a native
image with GraalVM. All of these options are configured for linux and windows and run automatically via GitHub
workflows.

## Building with jpackage

In this project jpackage is used to build a modular maven project into native binaries.

jpackage is configured via the `jpackage-maven-plugin`. Documentation and configuration options can be found
[here](https://github.panteleyev.org/jpackage-maven-plugin/jpackage-mojo.html). Further documentation to jpackage itself
can be found in the
[jpackage command documentation](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html).
Many options are made available via the plugin, analogue to the command line options. The `./jpackage` folder is set as
the resource folder for jpackage. It contains further configuration.

To build this goal, run one of the following. Note that the differences are the second profile selected via -P and you
can usually only build for the platform you are currently on:

`mvn -P linux-deb clean compile javafx:jlink jpackage:jpackage`

`mvn -P windows-exe clean compile javafx:jlink jpackage:jpackage`

`mvn -P app-image clean compile javafx:jlink jpackage:jpackage`

## Building a fat jar (removed)

Alternatively this project can be built into a fat jar to be executed with a jre which is provided by the end user.
To use this option you have to replace the `pom.xml` with the `pom-fat-jar-xml`.

To build this goal, run: `mvn -P fat-jar clean install package` (removed)

## Building with GraalVM (NOT WORKING, removed from workflow)

To build a native image with GraalVM, just pass the fat jar to GraalVM. However, GraalVM has some problems with
reflection and JavaFX is not yet supported. Maybe [Gluon](https://gluonhq.com/) is a valid alternative, but they have
a priced license in some cases and I could not be bothered to investigate it further.

The binaries created here, do not work as intended due to the reflection problems. They still require a jre (maybe even
more). Unless there is a way to create the binaries as standalone executables, there is no need to use GraalVM.

To build a native image with GraalVM, build the fat jar, setup GraalVM on your system and run:
`native-image -jar target/fx-demo.jar`
