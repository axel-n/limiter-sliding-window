package io.github.axel_n.limiter.annotation;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.dto.LimiterType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LimiterAspect {
    private final Map<LimiterType, Map<String, Limiter>> limiters;

    public LimiterAspect(List<Limiter> limiters) {
        Map<LimiterType, Map<String, Limiter>> limitersByImplementation = new HashMap<>();

        for (Limiter limiter : limiters) {
           Map<String, Limiter> limiterByName = limitersByImplementation.get(limiter.getLimiterType());

           if (limiterByName == null) {
               limiterByName = new HashMap<>();

           }

           limiterByName.put(limiter.getInstanceName(), limiter);
           limitersByImplementation.put(limiter.getLimiterType(), limiterByName);
        }

        this.limiters = limitersByImplementation;
    }

    @Around(value = "execution(public * *(..)) && @annotation(limiterConfig)", argNames = "joinPoint, limiterConfig")
    public Object process(ProceedingJoinPoint joinPoint, LimiterConfig limiterConfig) throws Throwable {

        // TODO check this logic at first time after startup
        Map<String, Limiter> limitersByName = limiters.get(limiterConfig.implementation());
        if (limitersByName == null) {
            throw new IllegalArgumentException("not found instance of limiter with type=" + limiterConfig.implementation());
        }

        // TODO check this logic at first time after startup
        Limiter limiter = limitersByName.get(limiterConfig.instanceName());
        if (limiter == null) {
            throw new IllegalArgumentException(String.format("not found instance of limiter with type=%s by name=%s",
                    limiterConfig.implementation(), limiterConfig.instanceName()));
        }

        switch (limiterConfig.limitType()) {
            case EXECUTE_OR_WAIT: {
                return limiter.executeOrWait(() -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            case EXECUTE_OR_THROW_EXCEPTION: {
                return limiter.executeOrThrowException(() -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            default: {
                throw new RuntimeException("not implemented"); // TODO how to hide this?
            }
        }
    }
}
