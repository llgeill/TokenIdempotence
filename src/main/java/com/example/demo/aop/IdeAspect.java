package com.example.demo.aop;

import com.alibaba.fastjson.JSONObject;

import com.example.demo.param.ResultVO;
import com.example.demo.util.HttpHelper;
import com.example.demo.util.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Slf4j
@Component
public class IdeAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    private final String msg = "token 失效，请刷新页面后再进行提交!";

    @Pointcut("@annotation(com.example.demo.annotation.Ide)")
    public void idePointCut() {

    }

    @Around("idePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        //获取request参数
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        JSONObject json = JSONObject.parseObject(HttpHelper.getBodyString(request));
        if (json == null || json.getString("ide") == null) {
            log.info("获取不到幂等性token ： ide");
            return ResultVOUtil.failMsg(msg);
        }
        String ide = json.getString("ide");
        //从redis获取token
        Map map = (Map) redisTemplate.opsForValue().get(ide);
        //如果方法名称正确且状态为1，则将状态置为0并且继续运行方法，否则抛出异常
        if (map != null && (methodName.equals(map.get("method")) && 1 == (int) map.get("status"))) {
            map.put("status", 0);
            redisTemplate.opsForValue().set(ide, map);
            log.info("幂等性token 消费成功 ！");
        } else {
            log.info("redis查询不到幂等性token ： ide");
            return ResultVOUtil.failMsg(msg);
        }
        //执行相应方法
        Object object = joinPoint.proceed();
        // 获取响应信息，如果成功则删除token，如果不成功则将token的状态state重新置为1
        ResultVO result = (ResultVO) object;
        if (null != result.getCode() && result.getCode() == 1) {
            redisTemplate.delete(ide);
            log.info("方法执行成功,幂等性token删除成功！");
        } else {
            map.put("status", 1);
            redisTemplate.opsForValue().set(ide, map);
            log.info("方法执行失败,幂等性token状态重置！");
        }
        return object;
    }


}