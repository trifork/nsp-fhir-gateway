package dk.medcom.edelivery;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TestContainerExtension.class)
@ContextConfiguration(initializers = TestContainerExtension.DBContainerInitializer.class)
public @interface UseTestContainers {
}
