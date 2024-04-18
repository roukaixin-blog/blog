package com.roukaixin.security.aspect;

import com.roukaixin.security.annotation.Limit;
import com.roukaixin.common.exception.LimitException;
import com.roukaixin.common.utils.IpUtils;
import com.roukaixin.security.constant.RedisConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 *
 * @author 不北咪
 * @date 2024/3/24 下午10:44
 */
@Aspect
@Component
@Slf4j
public class LimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public LimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(limit)")
    public void pointcut(Limit limit) {

    }

    @Before(value = "pointcut(limit)", argNames = "joinPoint,limit")
    public void doBefore(JoinPoint joinPoint, Limit limit) {
        long time = limit.time();
        long count = limit.count();
        // 获取保存在 zset 中的 key
        String key = getZSetKey(joinPoint);
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        // 保存到 redis 中
        long millis = System.currentTimeMillis();
        zSet.add(key, millis, millis);
        // 设置过期时间，防止浪费内存
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
        // 移除 {time} 秒之前的访问记录（滑动窗口思想）, 根据 score 范围删除
        zSet.removeRangeByScore(key, 0, millis - time * 1000);

        // 获取 key 中的条数
        Long redisCount = zSet.zCard(key);
        if (redisCount != null && redisCount.compareTo(count) > 0) {
            // 限流
            throw new LimitException();
        }
    }

    private String getZSetKey(JoinPoint joinPoint) {
        StringBuilder key = new StringBuilder(RedisConstant.LIMIT);
        // 获取 request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = IpUtils.getIp(request);
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 方法对象
        Method method = signature.getMethod();
        // 方法所在的类 class 对象
        Class<?> declaringClass = method.getDeclaringClass();
        // 全类名
        String className = declaringClass.getName();
        // 方法名
        String methodName = method.getName();
        return key
                .append(className)
                .append(RedisConstant.COLON)
                .append(methodName)
                .append(RedisConstant.COLON)
                .append(ip)
                .append(RedisConstant.COLON)
                .toString();
    }
}
