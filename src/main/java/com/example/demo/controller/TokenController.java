package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.annotation.Ide;
import com.example.demo.param.ResultVO;
import com.example.demo.util.ResultVOUtil;
import com.example.demo.util.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class TokenController {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * @param jsonObject 接受一个name为method的json，值为请求名称,例如 {"method":"test"}
     * @return token值
     */
    @RequestMapping("/token")
    public ResultVO getMethodsToken(@RequestBody JSONObject jsonObject) {
        Map map = new HashMap();
        if (jsonObject == null || jsonObject.getString("method") == null) {
            return ResultVOUtil.fail();
        }
        String method = jsonObject.getString("method");
        String ide = SnowflakeIdUtils.getToken();
        //设置请求方法名称以及token状态
        map.put("method", method);
        map.put("status", 1);
        redisTemplate.opsForValue().set(ide, map);
        return ResultVOUtil.success(ide);
    }

    /**
     * 加上@Id注解则需要验证是否携带token
     * @return
     */
    @RequestMapping("/test")
    @Ide
    public ResultVO test() {
        return ResultVOUtil.success();
    }


}
