package com.example.demo;

import com.example.demo.param.ResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class DemoApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    private ResultVO ideToken;
    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 获取token
     * @throws Exception
     */
    @Before
    public void contextLoads() throws Exception {
        String jsonStr = "{\"method\":\"test\"}";
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post("/token").content(jsonStr.getBytes())
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
         .andReturn().getResponse().getContentAsByteArray();
        MockMvcResultMatchers.content();
        ideToken = objectMapper.readValue(body, ResultVO.class);
        log.info("ideToken : " + ideToken);
    }

    /**
     * 携带token并访问幂等性接口
     * @throws Exception
     */
    @Test
    public void testRequest() throws Exception {
        String jsonStr = "{\"ide\":\""+ideToken.getDatas()+"\"}";
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post("/test").content(jsonStr).
                contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsByteArray();
        MockMvcResultMatchers.content();
        ResultVO resultVO= objectMapper.readValue(body, ResultVO.class);
        Assert.assertEquals(Optional.ofNullable(resultVO.getCode()), Optional.of(1));
    }


    /**
     * 携带token并访问幂等性接口2此
     * @throws Exception
     */
    @Test
    public void testRequestTwo() throws Exception {
        String jsonStr = "{\"ide\":\""+ideToken.getDatas()+"\"}";
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post("/test").content(jsonStr).
                contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsByteArray();
        MockMvcResultMatchers.content();
        ResultVO resultVO= objectMapper.readValue(body, ResultVO.class);
        Assert.assertEquals(Optional.ofNullable(resultVO.getCode()), Optional.of(1));


        byte[] body2 = mockMvc.perform(MockMvcRequestBuilders.post("/test").content(jsonStr).
                contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsByteArray();
        MockMvcResultMatchers.content();
        ResultVO resultVO2= objectMapper.readValue(body2, ResultVO.class);
        Assert.assertEquals(Optional.ofNullable(resultVO2.getCode()), Optional.of(2));
    }

    /**
     * 不携带token并访问幂等性接口
     * @throws Exception
     */
    @Test
    public void testRequestNoToken() throws Exception {
        String jsonStr = "{\"nonono\":\""+ideToken.getDatas()+"\"}";
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post("/test").content(jsonStr).
                contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsByteArray();
        MockMvcResultMatchers.content();
        ResultVO resultVO= objectMapper.readValue(body, ResultVO.class);
        Assert.assertEquals(Optional.ofNullable(resultVO.getCode()), Optional.of(2));
    }



}
