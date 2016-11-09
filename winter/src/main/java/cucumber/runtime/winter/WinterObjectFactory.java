package cucumber.runtime.winter;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import com.github.ledoyen.automocker.internal.TestContextManagers;
import com.github.ledoyen.winter.internal.AutomockerFullConfiguration;

import cucumber.api.java.ObjectFactory;
import cucumber.runtime.CucumberException;

/**
 * Largely pasted from SpringFactory as extending Cucumber is such a pain.
 */
public class WinterObjectFactory implements ObjectFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(WinterObjectFactory.class);
	private static WinterObjectFactory INSTANCE;

	private ConfigurableListableBeanFactory beanFactory;
	private CucumberTestContextManager testContextManager;

	private final Collection<Class<?>> stepClasses = new HashSet<Class<?>>();
	private Class<?> stepClassWithSpringContext = null;

	public WinterObjectFactory() {
		LOGGER.info("Cucumber starts using Winter ObjectFactory");
		INSTANCE = this;
	}

	public static WinterObjectFactory getInstance() {
		return INSTANCE;
	}

	public void addContextClass(Class<?> contextClass) {
		if (dependsOnSpringContext(contextClass)) {
			stepClassWithSpringContext = contextClass;
		}
	}

	@Override
	public boolean addClass(final Class<?> stepClass) {
		if (!stepClasses.contains(stepClass)) {
			if (dependsOnSpringContext(stepClass)) {
				if (stepClassWithSpringContext == null) {
					stepClassWithSpringContext = stepClass;
				} else {
					checkAnnotationsEqual(stepClassWithSpringContext, stepClass);
				}
			}
			stepClasses.add(stepClass);
		}
		return true;
	}

	public static boolean dependsOnSpringContext(Class<?> type) {
		boolean hasStandardAnnotations = annotatedWithSupportedSpringRootTestAnnotations(type);

		if (hasStandardAnnotations) {
			return true;
		}

		final Annotation[] annotations = type.getDeclaredAnnotations();
		return (annotations.length == 1) && annotatedWithSupportedSpringRootTestAnnotations(annotations[0].annotationType());
	}

	private static boolean annotatedWithSupportedSpringRootTestAnnotations(Class<?> type) {
		return type.isAnnotationPresent(ContextConfiguration.class);
	}

	private void checkAnnotationsEqual(Class<?> stepClassWithSpringContext, Class<?> stepClass) {
		Annotation[] annotations1 = stepClassWithSpringContext.getAnnotations();
		Annotation[] annotations2 = stepClass.getAnnotations();
		if (annotations1.length != annotations2.length) {
			throw new CucumberException("Annotations differs on glue classes found: " + stepClassWithSpringContext.getName() + ", " + stepClass.getName());
		}
		for (Annotation annotation : annotations1) {
			if (!isAnnotationInArray(annotation, annotations2)) {
				throw new CucumberException("Annotations differs on glue classes found: " + stepClassWithSpringContext.getName() + ", " + stepClass.getName());
			}
		}
	}

	private boolean isAnnotationInArray(Annotation annotation, Annotation[] annotations) {
		for (Annotation annotationFromArray : annotations) {
			if (annotation.equals(annotationFromArray)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
		if (stepClassWithSpringContext != null) {
			testContextManager = new CucumberTestContextManager(stepClassWithSpringContext);
		} else {
			throw new CucumberException("No Spring Context available, supply one with @ContextConfiguration or use SpringFactory (cucumber-spring) instead");
		}
		notifyContextManagerAboutTestClassStarted();
		if (beanFactory == null || isNewContextCreated()) {
			beanFactory = testContextManager.getBeanFactory();
			for (Class<?> stepClass : stepClasses) {
				registerStepClassBeanDefinition(beanFactory, stepClass);
			}
		}
		GlueCodeContext.INSTANCE.start();
	}

	private void registerStepClassBeanDefinition(ConfigurableListableBeanFactory beanFactory, Class<?> stepClass) {
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(stepClass).setScope(GlueCodeScope.NAME).getBeanDefinition();
		registry.registerBeanDefinition(stepClass.getName(), beanDefinition);
	}

	private boolean isNewContextCreated() {
		return !beanFactory.equals(testContextManager.getBeanFactory());
	}

	private void notifyContextManagerAboutTestClassStarted() {
		try {
			testContextManager.beforeTestClass();
		} catch (Exception e) {
			throw new CucumberException(e.getMessage(), e);
		}
	}

	private void notifyContextManagerAboutTestClassFinished() {
		try {
			testContextManager.afterTestClass();
		} catch (Exception e) {
			throw new CucumberException(e.getMessage(), e);
		}
	}

	@Override
	public void stop() {
		notifyContextManagerAboutTestClassFinished();
		GlueCodeContext.INSTANCE.stop();
	}

	@Override
	public <T> T getInstance(Class<T> type) {
		try {
			return beanFactory.getBean(type);
		} catch (BeansException e) {
			throw new CucumberException(e.getMessage(), e);
		}
	}

	class CucumberTestContextManager extends TestContextManager {

		public CucumberTestContextManager(Class<?> testClass) {
			super(TestContextManagers.createTestContextBootstrapper(testClass, AutomockerFullConfiguration.class));
			registerGlueCodeScope(getContext());
		}

		public ConfigurableListableBeanFactory getBeanFactory() {
			return getContext().getBeanFactory();
		}

		private ConfigurableApplicationContext getContext() {
			return (ConfigurableApplicationContext) getTestContext().getApplicationContext();
		}

		private void registerGlueCodeScope(ConfigurableApplicationContext context) {
			do {
				context.getBeanFactory().registerScope(GlueCodeScope.NAME, new GlueCodeScope());
				context = (ConfigurableApplicationContext) context.getParent();
			} while (context != null);
		}
	}
}
