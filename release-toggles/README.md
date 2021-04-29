# Release Toggles

Release toggles are implemented on top of the [Togglz Java Library](https://www.togglz.org/).

## Configuration and Set-up

### Local Development Environment

To use release toggles in the local development update `application-release-toggles.yml.properties`.

Update the property `release-toggles-list` to specify the release toggles that you want to enable locally.
```
togglz:
  console:
    enabled: true
  release-toggles-list: VJAP-23,VJAP-24
```

### Togglz Admin Console

The Togglz admin console displays all the backend release toggles and their state at runtime.

The Togglz admin console is available in read-only mode.

To access it go to `<microservice>/togglz` and login with the togglz `admin` user.

**Example:**

* [http://localhost:8080/togglz](http://localhost:8080/togglz)


## Development Guidelines


### Naming Convention

Release toggle names must be created using JIRA ticket keys with an underscore as a separator, e.g. `VJAP_123`.


### Usage


#### Add New Release Toggle


1. All new release toggles must be added to `ReleaseToggles.java`

Each release toggle in `ReleaseToggles.java` should have a description which can be added using the `@Label` annotation. The description must
contain the following information: `"<JIRA-KEY>: <SHORT_DESCRIPTION>"`.

**Example:**

```java
public enum ReleaseToggles implements Feature {

  @Label("VJAP-123: A test release toggle")
  VJAP_123;

  ...

}
```


#### Toggle the Source Code

##### `if..else` Statements

Turn on/off parts of the code using `if..else` statements.

**Example:**

```java
@RestController
@RequestMapping("/new/")
public class NewFeatureRestController {

  @RequestMapping("new-feature")
  public ResponseEntity<?> newFeature() {
    if (VJAP_123.isActive()) {
      return ResponseEntity.ok().body("VJAP_123 is enabled!");
    }
    throw new ReleaseToggleNotEnabledException();
  }

}

```


##### Spring AOP and Annotations

Using Spring AOP (Aspect Oriented Programming) you can intercept methods with an
annotation that provides a release toggle name, and determine whether to continue executing the methods
depending on if the release toggle is enabled or not.

The `ReleaseToggleAspect.java` intercepts all method calls annotated with the
`@ReleaseToggle` annotation and checks the release toggle provided in the annotation parameter to determine whether it is active or not.
If the release toggle is active, the aspect will continue the execution of the method; if not,
it will log a message and throw a `ReleaseToggleNotEnabledException` without running the method code.

The `@ReleaseToggle` annotation can be used in any Spring bean.

For endpoints, the `ReleaseToggleNotEnabledException` is also annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)` to map to a HTTP 404 status code.

**Spring MVC example:**

```java
@RestController
@RequestMapping("/new/")
public class NewFeatureRestController {

  @RequestMapping("new-feature")
  @RequiresReleaseToggle("VJAP_123")
  public ResponseEntity<?> newFeature() {
    return ResponseEntity.ok().body("VJAP-123 is enabled!");
  }

}

```

##### ReleaseToggleBeanFactory

Using a `ReleaseToggleBeanFactory` it is possible to use a different bean implementation class
depending on whether a release toggle is on or off. Both implementations need to implement a common interface or have a common super-class.

```java

  @Bean
  public ReleaseToggleBeanFactory<SuperService> superServiceFactory(SuperCloudService cloudService,
      SuperLocaleService localeServe, FeatureManager featureManager) {
    return new ReleaseToggleProxyBeanFactory<>(
        SuperService.class, // the interface
        cloudService, // Release toggle active
      localeServe, //  Release toggle inactive
        ReleaseToggles.VJAP_123,
        featureManager
    );
  }
  
    @Bean
    public OtherBean otherBean(SuperService superService) {
      // either SuperCloudService or SuperLocaleService depending on VJAP_123 toggle
    }

```

#### Testing Release Toggles

The following two scenarios **must be tested** for each release toggle:

1. Release toggle disabled.
2. Release toggle enabled.

Since release toggles require all necessary Togglz Spring beans to be configured, each unit/integration test
context configuration must load `TogglzAutoConfiguration.class` and `CommonReleaseToggleConfig.class`.

**Example:**

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TogglzAutoConfiguration.class, ReleaseToggleConfiguration.class})
@DirtiesContext(classMode = AFTER_CLASS)
class ReleaseTogglesTest {

}
```

**ExampleReleaseTogglesIntegrationTest**

```java
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
class ExampleReleaseTogglesIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @AllEnabled(ExampleReleaseToggles.class)
  void testDisabled() {
    ...
  }

  @Test
  @AllDisabled(ExampleReleaseToggles.class)
  void testEnabled() {
    ...
  }

}
```


