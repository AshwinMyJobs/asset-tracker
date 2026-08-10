package com.enterprise.asset_service.config.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
@Component
public class DataSourceRoutingAspect {

    // Intercepts any repository method execution
    @Around("execution(* com.enterprise.asset_service.repository..*.*(..)) || @annotation(org.springframework.transaction.annotation.Transactional)")
    public Object routeTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Transactional transactional = method.getAnnotation(Transactional.class);

        // Fallback to check if the annotation sits at the class level instead of the method level
        if (transactional == null) {
            transactional = joinPoint.getTarget().getClass().getAnnotation(Transactional.class);
        }

        if (transactional != null && transactional.readOnly()) {
            System.out.println("🔀 Distributed Routing Mechanism: Activating REPLICA_READ Connection Node [Port: 5435]");
            DataSourceContextHolder.set(DataSourceType.REPLICA_READ);
        } else {
            System.out.println("🔀 Distributed Routing Mechanism: Activating PRIMARY_WRITE Connection Node [Port: 5433]");
            DataSourceContextHolder.set(DataSourceType.PRIMARY_WRITE);
        }

        try {
            return joinPoint.proceed(); // Executes your database query
        } finally {
            DataSourceContextHolder.clear(); // Clean up thread local context state safely
        }
    }
}





