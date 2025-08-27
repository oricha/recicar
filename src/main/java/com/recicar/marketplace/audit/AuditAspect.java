package com.recicar.marketplace.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Before("execution(* com.recicar.marketplace.service.*Service.*(..)) && !execution(* com.recicar.marketplace.service.CustomUserDetailsService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Before method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.recicar.marketplace.service.*Service.*(..)) && !execution(* com.recicar.marketplace.service.CustomUserDetailsService.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("After method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.recicar.marketplace.service.*Service.*(..)) && !execution(* com.recicar.marketplace.service.CustomUserDetailsService.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("After method: {} with exception: {}", joinPoint.getSignature().toShortString(), error.getMessage());
    }
}
