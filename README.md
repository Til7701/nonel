> how to build a JavaFX application into native binaries

[![.github/workflows/native.yaml](https://github.com/Til7701/javafx-native-image-sample/actions/workflows/native.yaml/badge.svg)](https://github.com/Til7701/javafx-native-image-sample/actions/workflows/native.yaml)

# Known TODOs

- `XSetErrorHandler() called with a GDK error trap pushed. Don't do that.` when running on linux. Seems to be a known
  problem since 2016: [JDK-8156779](https://bugs.openjdk.org/browse/JDK-8156779)
- maybe use maven plugin management
- add icons in other resolutions

# Native Package Example for JavaFX Application

Everyone always talks about how to code fancy stuff in all kinds of programming languages, but never about how to deploy
what you made in a user-friendly way. This repository wants to change that. It is an example of how to package a JavaFX
application into native binaries. Specifically, a `deb` package for Linux and a `setup.exe` for Windows users.

In the following chapters I will explain how this is done and what you have to change, if you want to use this process
for you own application.

## The JavaFX application

The folder `src/` contains a normal, modular JavaFX application. For this example it could be reduced a little bit more,
however I think these two additions might be interesting.

Firstly, JavaFX provides preloaders. Before the application is started, the preloader is run. You can set the preloader
by calling `System.setProperty("javafx.preloader", "de.holube.demo.MyPreloader");` before you call `launch()` for the
application. The class to provide should extend `Preloader`. I don't know whether this is best practice, but it is
nice to know.

Secondly, in `MyApplication` is a method called `setupIcons`. This method is called in the start method and sets the
icon `src/main/resources/icons/icon.png` in the application bar and taskbar/dock. These icons are not always be set
automatically. So here they are set manually at runtime.

## Maven

The dependencies and build are configured with maven in the `pom.xml`. So lets break it down.

At the top, there are some properties, used to define versions of dependencies and plugins. Under that are some
dependencies. JavaFX is necessary for obvious reasons, but you could remove lombok and the test frameworks. If you
remove lombok, you have to de-lombok first. In this project it is only one `@RequiredArgsConstructor`and the annotation
processor for lombok in the `maven-compiler-plugin` configuration.

Next are the profiles. Here they are used to configure jpackage. So we will cover them in that chapter. In general,
profiles provide optional dependencies and configuration for plugins, which can be activated by adding the
`-P profile-id` option to the maven build command.

## JLink

JLink creates a custom JRE, which only contains the classes needed for this application. It is configured via the
`javafx-maven-plugin` and called by the `javafx:jlink` goal.

# Old README

This sample app is built with jpackage. In previous iterations of this project, it could also be built into a fat jar
and further into a native
image with GraalVM. However, these two options have been removed to reduce complexity of the build process.

> Note: the deb package should be installed via `dpkg`.

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
