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
            <version>0.0.6</version>
        </dependency> 
    </dependencies>
```

## Gradle

Add to build.gradle:

```
    dependencies {
        compile "ch.uprisesoft:yali:0.0.6"
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
    public class Main {

        public static void main(String[] args) {
            new Main().run();
        }

        public void run() {
            Interpreter it = new Interpreter();
            it.loadStdLib();

            String input = "make \"greeting [Hello World!]\n";
            Node result = it.run(it.read(input));
            System.out.println(result);
        }
    }
```

The interpreter always returns the last evaluated result. You can check it's type
and cast it to the appropriate class type:

```
    List resultList = result.toList();        
    System.out.println(resultList);
```

The contents of compound data types can be accessed via `getChildren()`:

```
    for(Node child: resultList.getChildren()) {
        System.out.println(child);
    }
```

You can get the content of a variable by accessing the interpreter environment:
```
    Environment env = it.env();
        
    Node variableContent = env.thing("greeting");
    System.out.println(variableContent);
```

## Input and Output

To keep input and output handling flexible, it is implemented as Observer Pattern.
This means that it is up to the user of this library to implement appropriate
methods. This is done by implementing the interfaces InputGenerator and
OutputObserver and passing them to the method `loadStdLib()`:

```
    OutputObserver oo = new OutputObserver() {
        @Override
        public void inform(String output) {
            System.out.println(output);
        }
    };

    InputGenerator ig = new InputGenerator() {
        @Override
        public String request() {
            return "requestedinput";
        }

        @Override
        public String requestLine() {
            return "requestedinputline\n";
        }
    };

    it.loadStdLib(oo, ig);

    input = "make \"greeting [Hello World!]\n"
                + "print :greeting\n";
        
    result = it.run(it.read(input));
```

This results in printing to the console. You can do whatever you want with the
output that the OutputObserver provides.