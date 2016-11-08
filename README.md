# Winter

Winter is a set of predefined [Cucumber](https://cucumber.io/docs/reference/jvm#java) Step Definitions interacting with [Spring-Automocker](https://github.com/ledoyen/spring-automocker) available mocks.

## Getting started
Use the JUnit runner indicating your **Spring** context through classic annotations:
```java
@RunWith(WinterJUnit4FeatureRunner.class)
@ContextConfiguration(classes = MyApplication.class)
public class FeatureLauncherTest {

}
```

By default feature files are looked for in the packae of your main test (`FeatureLauncherTest` in the example).
To override that behavior, use CucumberOptions, for example: `@CucumberOptions(features = "classpath:features")`

## Cucumber concerns
As Winter is now based on Cucumber, Cucumber-related features and limitations are present.

#### Cucumber options
You can use `@CucumberOptions` to customize your test structure and add custom Step Definitions.
Step Definitions supports Spring's annotation such as `Autowired` without any additional `@ContextConfiguration` if you use `WinterJUnit4FeatureRunner`.

#### Cucumber runner
This applies only if you are not using `WinterJUnit4FeatureRunner`, but `Cucumber` instead.
By default **Cucumber** does not work well with multiple `ObjectFactory` available in root package `cucumber.runtime`.
So dependencies like **cucumber-spring** or **cucumber-guice** will not cohabite nicely with **Winter**.  

To fix the problem you have two choises :
* Dedicate specific classpaths for specific executions through your build system
* Force cucumber to use one or another `ObjectFactory` by passing the adequate system property.
To force Winter usage
```
-D-Dcucumber.api.java.ObjectFactory=cucumber.runtime.winter.WinterObjectFactory
```

If you do not have another `ObjectFactory` in your classpath, Cucumber will nicely pickup the **Winter** one through its `cucumber.runtime.java.ObjectFactoryLoader`.

To benefit from **Winter** prefefined Step Definitions you will also have to indicate *gluecode* location:
```java
@RunWith(Cucumber.class)
@CucumberOptions(glue = { "classpath:com/github/ledoyen/winter/stepdef", "classpath:my/custom/stepdef" })
public class MvcApplicationCucumberTest {

}
```