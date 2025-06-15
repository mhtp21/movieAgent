package com.mcpdemo.movieAgent.mcp.inspector;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

@Aspect
@Component
@Slf4j
public class McpTrafficInspector {

    @Pointcut("within(com.mcpdemo.movieAgent.mcp.server..*)")
    public void mcpServerPointcut() {}

    @Around("mcpServerPointcut()")
    public Object inspectMcpTraffic(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        if (args == null || args[0] instanceof Mono) {
            log.debug("==> MCP Inspector: Entering reactive method [{}] with a Mono<Context>", methodName);
        } else {
            log.debug("==> MCP Inspector: Entering method [{}] with Context = {}", methodName, args);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        if (result instanceof Mono<?> resultMono) {
            return resultMono.doFinally(signalType -> {
                stopWatch.stop();
                log.debug("<== MCP Inspector: Exiting reactive method [{}] after stream completion. Signal: {}. Execution Time: {} ms",
                        methodName, signalType, stopWatch.getTotalTimeMillis());
            });
        } else {
            stopWatch.stop();
            log.debug("<== MCP Inspector: Exiting method [{}] with Model = {}. Execution Time: {} ms",
                    methodName, result, stopWatch.getTotalTimeMillis());
            return  result;
        }
    }

    @AfterThrowing(pointcut = "mcpServerPointcut()", throwing = "e")
    public void inspectMcpErrors(JoinPoint joinPoint, Throwable e) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("!!! MCP Inspector: Exception occurred in method [{}]. Exception: {}", methodName, e.getMessage());
    }
}