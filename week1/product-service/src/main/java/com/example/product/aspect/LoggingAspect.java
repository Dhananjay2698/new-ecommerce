package com.example.product.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Pointcut("execution(* com.example.product.service.*.*(..))")
    public void serviceMethods() {}
    
    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String fullMethodName = className + "." + methodName;
        
        long startTime = System.currentTimeMillis();
        
        // Log method entry with parameters
        logger.info("=== ENTERING METHOD: {} ===", fullMethodName);
        try {
            String params = objectMapper.writeValueAsString(joinPoint.getArgs());
            logger.info("Parameters: {}", params);
        } catch (Exception e) {
            logger.info("Parameters: [Could not serialize parameters]");
        }
        
        Object result = null;
        try {
            // Execute the method
            result = joinPoint.proceed();
            
            // Log method exit with return value and execution time
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            logger.info("=== EXITING METHOD: {} ({}ms) ===", fullMethodName, executionTime);
            try {
                String returnValue = objectMapper.writeValueAsString(result);
                logger.info("Return Value: {}", returnValue);
            } catch (Exception e) {
                logger.info("Return Value: [Could not serialize return value]");
            }
            
            return result;
            
        } catch (Exception e) {
            // Log exception with execution time
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            logger.error("=== EXCEPTION IN METHOD: {} ({}ms) ===", fullMethodName, executionTime);
            logger.error("Exception: {}", e.getMessage(), e);
            throw e;
        }
    }
} 