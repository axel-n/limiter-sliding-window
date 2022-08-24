package io.github.axel_n.limiter.annotation;


import io.github.axel_n.limiter.dto.LimiterType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.github.axel_n.limiter.dto.LimiterType.SLIDING_WINDOW;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LimiterConfig {

    LimiterType implementation() default SLIDING_WINDOW;

    String instanceName() default "common";

    ExecutionLimitType limitType() default ExecutionLimitType.EXECUTE_OR_WAIT;
}
