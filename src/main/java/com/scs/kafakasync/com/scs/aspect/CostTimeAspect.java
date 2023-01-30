package com.scs.kafakasync.com.scs.aspect;

import com.scs.kafakasync.com.scs.service.producer.ConsumerReplyService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author 大菠萝
 * @date 2023/01/20 18:51
 **/
@Aspect
@Component
public class CostTimeAspect {
    private static final Logger logger = LoggerFactory.getLogger(CostTimeAspect.class);

    @Pointcut(value = "@annotation(com.scs.kafakasync.com.scs.aspect.CostTime)")
    public void RunTime(){
    }


    @Around("RunTime()")
    public Object  doAround(ProceedingJoinPoint joinPoint) { //ProceedingJoinPoint可以获取当前方法和参数等信息
        Object obj = null;
        try {
            long beginTime = System.currentTimeMillis();
            obj = joinPoint.proceed();
            //获取方法名称
            String methodName = joinPoint.getSignature().getName();
            //获取类名称
            String className = joinPoint.getSignature().getDeclaringTypeName();
            logger.info("类:[{}]，方法:[{}]耗时时间为:[{}]", className, methodName, System.currentTimeMillis() - beginTime + "毫秒");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }

}