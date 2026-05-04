package com.ran.commons.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SpaceAuthAspect {
    @Pointcut("@annotation(com.ran.commons.annotation.space.SpacePreAuth)")
    public void annotatedMethod() {}

//    @Around("annotatedMethod()")

}
