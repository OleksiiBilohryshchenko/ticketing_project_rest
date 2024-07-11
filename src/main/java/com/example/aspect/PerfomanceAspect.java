package com.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class PerfomanceAspect {

    @Pointcut("@annotation(com.example.annotation.ExecutionTime)")
    public void executionTimePC(){}

    @Around("executionTimePC()")
    public Object aroundAnyExecutionTimeAdvice(ProceedingJoinPoint proceedingJoinPoint){

        long beforeTime = System.currentTimeMillis();

        Object result = null;
        log.info("Execution starts:");

        try{
            result = proceedingJoinPoint.proceed();
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }

        long afterTime = System.currentTimeMillis();

        log.info("Time taking to execute: {} ms - Method: {}",
                (afterTime - beforeTime), proceedingJoinPoint.getSignature().toShortString());
        return result;
    }


}
