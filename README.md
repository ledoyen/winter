# Winter

Winter is a set of predefined [Cucumber](https://cucumber.io/docs/reference/jvm#java) Step Definitions interacting with [Spring-Automocker](https://github.com/ledoyen/spring-automocker) available mocks.

## Getting started
Alter your **Maven** *pom.xml* with the following:

```xml
<project>
...
	<dependencies>
	...
		<dependency>
			<groupId>com.github.ledoyen.winter</groupId>
			<artifactId>winter</artifactId>
			<version>${winter.version}</version>
			<scope>test</scope>
		</dependency>
	</dependencies>
	...
	<build>
		...
		<plugins>
			...
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.19.1</version>
				<executions>
					<execution>
						<id>winter-test</id>
						<phase>test</phase>
						<goals><goal>test</goal></goals>
						<configuration>
							<test>WinterTestLauncher</test>
							<dependenciesToScan>
                				<dependency>com.github.ledoyen.winter:winter</dependency>
            				</dependenciesToScan>
            				<systemPropertyVariables>
								<winter.ContextConfiguration>your.ConfigurationClass</winter.ContextConfiguration>
							</systemPropertyVariables>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
</project>
```

By default feature files are looked for in the *features* test directory (src/test/resources/features).
To override that behavior, use [Cucumber options](https://cucumber.io/docs/reference/jvm#list-configuration-options).

## Cucumber concerns
As Winter is now based on Cucumber, Cucumber-related features and limitations are present.

#### Cucumber options
You can use [Cucumber options](https://cucumber.io/docs/reference/jvm#list-configuration-options) to customize your test structure and add custom Step Definitions.
Step Definitions supports Spring's annotation such as `Autowired` without any additional `@ContextConfiguration` if you use `WinterTestLauncher`.

#### Cucumber runner
This applies only if you are not using `WinterTestLauncher` or `WinterJUnit4FeatureRunner`, but `Cucumber` instead.
By default **Cucumber** does not work well with multiple `ObjectFactory` available in root package `cucumber.runtime`.
So dependencies like **cucumber-spring** or **cucumber-guice** will not cohabite nicely with **Winter**.  

To fix the problem you have two choises :
* Dedicate specific classpaths for specific executions through your build system
* Force cucumber to use one or another `ObjectFactory` by passing the adequate system property:
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
