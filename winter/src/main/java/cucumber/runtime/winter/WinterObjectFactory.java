package cucumber.runtime.winter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import cucumber.runtime.java.spring.SpringFactory;

/**
 * Sets up a {@link SpringFactory} that can be accessed outside Cucumber internal usage.
 */
public class WinterObjectFactory extends SpringFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinterObjectFactory.class);
    private static WinterObjectFactory INSTANCE;

    public WinterObjectFactory() {
        LOGGER.debug("Cucumber starts with Winter ObjectFactory");
        INSTANCE = this;
    }

    public static WinterObjectFactory getInstance() {
        return INSTANCE;
    }

    public void addContextClass(Class<?> contextClass) {
        if (ReflectionTestUtils.invokeMethod(this, "dependsOnSpringContext", contextClass)) {
            ReflectionTestUtils.setField(this, "stepClassWithSpringContext", contextClass);
        }
    }
}
