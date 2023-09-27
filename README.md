# DependencyInjector

### Set up

```xml

```

### Basic usage

```java

import es.dependencyinjector.dependencies.annotations.Service;

DependenciesRepository dependencies = new InMemoryDependenciesRepository();

DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
        .multiThreadedScan()
        .waitUntilCompletion()
        .packageToScan("you package")
        .customAnnotations(MyCustomAnnotation.class)
        .dependenciesRepository(dependencies)
        .useDebugLogging()
        .build());

ServiceA serviceA = dependencies.get(ServiceA.class);

@Service
class ServiceA { //Atleast one empty constructor
    private final serviceB;
}

@MyCustomAnnotation
class ServiceB { }

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface MyCustomAnnotation { }
```

### Abstractions

```java
AbstractionsRepository abstractions = new InMemoryAbstractionsRepository();
DependenciesRepository dependencies = new InMemoryDependenciesRepository();

//Useful when dealing with external libraries
abstractions.add(/*Abstraction class*/, /*Implementation class*/);

DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
    .multiThreadedScan()
    .packageToScan("your package")
    .waitUntilCompletion()
    .dependenciesRepository(dependencies) //Optional
    .abstractionsRepository(abstractions) //Optional
    .useDebugLogging()
    .build());

dependencies.get(Abstraction.class).greet();
Class<Implementation> implementation = (Class<Implementation>) abstractions.get(Abstraction.class);

interface Abstraction {
    void greet();
}

@Component
class Implementation implements Abstraction {
    @Override
    public void greet() {
        System.out.println("Hello!");
    }
}
```

### Providers
```java
DependenciesRepository dependencies = new InMemoryDependenciesRepository();

DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
        .multiThreadedScan()
        .packageToScan("your package")
        .waitUntilCompletion()
        .dependenciesRepository(dependencies)
        .useDebugLogging()
        .build());

DateFormat dateFormat = dependencies.get(DateFormat.class);

//Providers can depend on other dependencies but not on other providers
@Component
public class DateFormatterProvider {
    @Provider
    public DateFormat dateFormat() {
        return new SimpleDateFormat();
    }
}
```

### Indexing 

#### filterByImplementsInterface()

```java
DependenciesRepository dependencies = new InMemoryDependenciesRepository();

DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
        .packageToScan("your package")
        .waitUntilCompletion()
        .dependenciesRepository(dependencies)
        .build());

for (Abstraction abstraction : dependencies.filterByImplementsInterface(Abstraction.class)) {
    abstraction.printInConsole();
}

interface Abstraction {  void printInConsole(); }

@Service
public class ImplementationA implements Abstraction {
    @Override
    public void printInConsole() {
        System.out.println("I'm ImplementationA");
    }
}

@Service
public class ImplementationB implements Abstraction {
    @Override
    public void printInConsole() {
        System.out.println("I'm ImplementationB");
    }
}
```
#### filterByImplementsInterfaceWithGeneric()
```java
DependenciesRepository dependencies = new InMemoryDependenciesRepository();

DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
        .packageToScan("your package")
        .waitUntilCompletion()
        .dependenciesRepository(dependencies)
        .build());

dependencies.filterByImplementsInterfaceWithGeneric(Abstraction.class, String.class).ifPresent(implementation -> {
    implementation.printInConsole(); //Will print "I'm ImplementationA With String"
});

interface Abstraction<T> {
    void printInConsole();
}

@Service
public class ImplementationA implements Abstraction<String> {
    @Override
    public void printInConsole() {
        System.out.println("I'm ImplementationA With String");
    }
}

@Service
public class ImplementationB implements Abstraction<Integer> {
    @Override
    public void printInConsole() {
        System.out.println("I'm ImplementationB with Integer");
    }
}
```

