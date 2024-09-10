# troubleshooters-hub

## Project background:

In the fast-paced and highly competitive software industry, innovation often focuses on large-scale solutions, leaving smaller, niche problems overlooked. This semester's project theme: “Small Audience, Real Impact”, challenges you to identify and address specific issues faced by individuals or small groups within your community. Form a team of students in your practical, and brainstorm tailored solutions that can make a meaningful difference in the lives of you preferred audience.

What is a small audience? This is up to you to decide, but you can think of this as any group that needs further qualification besides just saying 'users'. You can focus on a local community with a problem to solve (via software) that is unique to their geographical area. Or you can think about a group of users sharing in a passion for a particular game or craft. Or else you can think of groups of users sharing in a personal, cultural, linguistic, educational, social, or other background. 

## Minimal Requirements

Each project will need to provide a certain amount of features/functionalities, so, even when not explicitly mentioned in the project idea, consider that you will need to have:

- a graphical user interface with windows for the main functionalities, in general based on JavaFX
- an authentication system to sign-up/sign-in (GUI and models)
- a persistency system to store/retrieve/update user data (GUI and models)
- one or (more likely) more application windows in which the actual, useful part of the application is performed (again, with GUI and models)

## Developer's Guide

### Getting Started

1. Open the repository in IntelliJ IDEA and click 'Build' to automatically download the dependencies and build the project.

2. In the root directory of the project, navigate to the `./scripts` directory and run the `initdb.sh` (`$ sh initdb.sh`) script if you are on Linux / MacOS or the `Initialize-Database.ps1` script if you are on Windows to create the DB schema and populate it with some test data.

3. Run the `SoundLinkApplication` class located in the `src/main/java/hub/troubleshooters/soundlink/app` directory to start the application.

### Project Structure

This project uses the familiar layered architecture pattern, with the following packages and their respective responsibilities:

- `hub.troubleshooters.soundlink.app` - contains the main class to start the application and acts as the **application and presentation layers**.

- `hub.troubleshooters.soundlink.core` - contains the core logic and acts as the **business layer** of the application.

- `hub.troubleshooters.soundlink.data` - contains the **data access layer** of the application.

As a general rule, the **application layer** should only contain code that is directly related to the user interface, such as controllers, views, and view models. The **business layer** should contain the core logic of the application, and the **data access layer** should contain code that interacts with the database. The application layer should only communicate with the business layer, and the data access layer should only communicate with the business layer.

## Adding a new table to the database

For any new table created in the database, the following steps should be followed:

1. create the table in the `/database.sql` file
2. create a corresponding model in the `data.models` package
3. create a relevant factory in the `data.models.factories` package which extends `ModelFactory` and implement the needed methods
4. register the factory as a singleton in the `app.AppModule` class

Now, whenever you want to create, read, update, and destroy any objects of that DB type, you can inject the factory you just created into the necessary service.

e.g.

```java
// this example file would be located at core.auth.LoginService

private final UserFactory userFactory;

// injecting the newly created UserFactory into our service using the @Inject decorator
@Inject
public LoginService(UserFactory userFactory) {
    this.userFactory = userFactory;
}

public void login(String username) {
    User user = userFactory.get(username);  // using the injected UserFactory to call the DB to get the user.
    // ...
}
```

## Adding a new feature to the application

For any new feature added to the application, the following steps should be followed:

1. create a new branch from the `main` branch with the name `feature/<JIRA issue number>`, e.g. `feature/SL-10` if it's a feature or `bugfix/SL-10` if it's a bug fix.

2. after completing the feature, create a pull request to merge the feature branch into the `main` branch (or any other branch that the feature is based on). Assign at least one reviewer to the pull request. Assigning isn't necessary if it's a simple bugfix but is recommended for features.

3. after the pull request is approved, merge the feature branch into the `main` branch and delete the feature branch.


## Adding tests to the application

Testing is done using [JUnit 5](https://junit.org/junit5/) and [Mockito](https://site.mockito.org/). Mockito is a mocking framework that allows you to create mock objects and stub the behavior of the methods of these objects which is useful we're using Dependency Injection, we can easily inject mock objects into the unit of code we're testing.

To add a new test to the application, the following steps should be followed:

1. create a new test class in the `test` directory with the same package structure as the class you're testing. The test class should have the same name as the class you're testing with the suffix `Test`, e.g. `LoginServiceTest` if you're testing the `LoginService` class.

2. If you're testing a class that has dependencies, you should mock these dependencies using Mockito. You can do this by annotating the dependencies with `@Mock`. You can then inject these dependencies into the class you're testing using the `@InjectMocks` annotation. Follow the example in `test/java/hub/troubleshooters/soundlink/core/auth/LoginServiceImplTest.java`.