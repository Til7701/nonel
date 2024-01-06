> how to build a JavaFX application into native binaries with Maven using JLink and JPackage

[![.github/workflows/native.yaml](https://github.com/Til7701/javafx-native-image-sample/actions/workflows/native.yaml/badge.svg)](https://github.com/Til7701/javafx-native-image-sample/actions/workflows/native.yaml)

# Known TODOs

- `XSetErrorHandler() called with a GDK error trap pushed. Don't do that.` when running on linux. Seems to be a known
  issue since 2016: [JDK-8156779](https://bugs.openjdk.org/browse/JDK-8156779)
- maybe use maven plugin management
- add icons in other resolutions
- figure out, why apt does not run installation scripts properly

# Native Package Example for JavaFX Application

Everyone always talks about how to code fancy stuff in all kinds of programming languages, but never about how to deploy
what you made in a user-friendly way. This repository wants to change that. It is an example of how to package a JavaFX
application into native binaries. Specifically, a `deb` package for Linux and a `setup.exe` for Windows users. It was
a wild ride of trial and error `:)`

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

JLink builds the application into a native binary and creates a custom JRE, which only contains the classes needed for this application. It is configured via the
`javafx-maven-plugin` and called by the `javafx:jlink` goal. By adding the following to the plugin configuration, a lot
of things are removed. These are not important for the end user.

```xml

<configuration>
    <stripDebug>true</stripDebug>
    <compress>1</compress>
    <noHeaderFiles>true</noHeaderFiles>
    <noManPages>true</noManPages>
</configuration>

```

## JPackage

`jpackage` is a command line tool, which can package the binary built by `jlink` into a native setup binary. 
Documentation can be found [here](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html).

In this project jpackage is configured by the `jpackage-maven-plugin` from `org.panteleyev`. Documentation for the
plugin can be found [here](https://github.panteleyev.org/jpackage-maven-plugin/jpackage-mojo.html).

The application can be built with three different targets. A .deb package, a windows setup executable and an app-image
for the os you are on. JPackage provides more options, but these are the ones, which are configured here. So lets take
a look at each of them.

Also note the resource directory set by `<resourceDir>./jpackage</resourceDir>`. I will refer to this for the different
goals.

### DEB Package

#### Configuration

The maven profile `linux-deb` adds a bit of configuration to the `jpackage-maven-plugin`.

```xml

<configuration>
    <type>DEB</type>
    <linuxShortcut>true</linuxShortcut>
    <linuxPackageName>fx-demo</linuxPackageName>
    <linuxAppCategory>Utilities</linuxAppCategory>
    <linuxMenuGroup>Utilities</linuxMenuGroup>
</configuration>

```

This configuration also uses the resource directory.

The icon for the app is automatically `PublicDemoName.png` due to the name set in the common plugin configuration.

DEB packages have some scripts, which are called automatically by `dpkg`. Here two of them are overridden to add some
functionality. The scripts are pretty self-explanatory. Documentation on these scripts in general can be found
[here](https://www.debian.org/doc/debian-policy/ch-maintainerscripts.html).

#### Building the package

You can build the package by running:

`mvn -P jpackage -P linux-deb clean compile javafx:jlink jpackage:jpackage`

The package can be found in `target/dist/`.

#### Installation

To install this package on your computer, download the `.deb` file from the latest release and run:

```shell
dpkg -i fx-demo_amd64.deb
```

> Note: In our testing, installing via `apt` did **not** work properly. So you should use `dpkg`.

### Windows Setup EXE

#### Configuration

The maven profile `windows-exe` adds a bit of configuration to the `jpackage-maven-plugin`.

```xml

<configuration>
    <type>EXE</type>
    <winMenu>true</winMenu>
    <winUpgradeUuid>ed0a813c-d868-488d-9c23-1e16f0f4e8f6</winUpgradeUuid>
</configuration>

```

This configuration also uses the resource directory.

The icon for the app is automatically `PublicDemoName.ico` due to the name set in the common plugin configuration.

#### Building the package

You can build the package by running:

`mvn -P jpackage -P windows-exe clean compile javafx:jlink jpackage:jpackage`

The setup exe can be found in `target/dist/`.

#### Installation

To install this package on your computer, download the `.exe` file from the latest release and run it. It is not signed,
so windows will ask you whether you really want to run it. Click "more information" and "Run Anyway". You can now find
the application in your list of programs in the start menu.

### App Image

This should not be confused with the package format `AppImage` for linux. This option creates a standalone zip, which
has a launcher script and only runs on the system it was built for.

#### Configuration

The maven profile `app-image` adds a bit of configuration to the `jpackage-maven-plugin`.

```xml

<configuration>
    <type>APP_IMAGE</type>
</configuration>

```

This configuration also uses the resource directory.

The icon for the app is automatically `PublicDemoName.png` due to the name set in the common plugin configuration.

#### Building the package

You can build the package by running:

`mvn -P jpackage -P app-image clean compile javafx:jlink jpackage:jpackage`

The image can be found in `target/dist/`.

#### Installation

To install this package on your computer, download the `.exe` file from the latest release and run it. It is not signed,
so windows will ask you whether you really want to run it. Click "more information" and "Run Anyway". You can now find
the application in your list of programs in the start menu.

# Other Options

The following describes options to get a similar result. However, they are not implemented in this repository.

## Building a fat jar

You could provide your users with a fat jar (jar with dependencies). Users will still need a jre installed on their
computers. So this option is not very user-friendly.

## Building with GraalVM

To build a native image with GraalVM, just pass the fat jar to GraalVM. However, GraalVM has some problems with
reflection and JavaFX is not yet supported. Maybe [Gluon](https://gluonhq.com/) is a valid alternative, but they have
a priced license in some cases and I could not be bothered to investigate it further.

To build a native image with GraalVM, build the fat jar, setup GraalVM on your system and run:
`native-image -jar target/fx-demo.jar`

> Note: The jar has to be built with GraalVM as well.

# Further Reading
- how to provide a PPA repository via GitHub pages to distribute DEB packages: [JayPi4c/ppaDemo](https://github.com/JayPi4c/ppaDemo)
- how to publish a DEB package to the PPA repository: [JayPi4c/ExampleDebianPackage](https://github.com/JayPi4c/ExampleDebianPackage)

# Thanks to [JayPi4c](https://github.com/JayPi4c) for testing and helping
