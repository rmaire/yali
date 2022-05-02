# Yali

Yali (Yet another Logo implementation) is a programming language aiming at learning and exploring. It's primary goals are:

# Building

```
    mvn clean
    mvn package
    mvn -Pgithub deploy
```

# Quickstart
## Maven

Add to pom.xml:

```
    <dependencies>
        <dependency>
            <groupId>ch.uprisesoft</groupId>
            <artifactId>yali</artifactId>
            <version>0.0.5</version>
        </dependency> 
    </dependencies>
```

## Gradle

Add to build.gradle:

```
    dependencies {
        compile "ch.uprisesoft:yali:0.0.5"
    }

    repositories {
        jcenter()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rmaire/yali")
        }
    }
```

## Running a simple script

Running a script can be done with a few lines of code:

```
Interpreter it = new Interpreter();

String input = "print [Hello World!]\n";
System.
```