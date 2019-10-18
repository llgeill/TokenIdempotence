package com.example.demo.util;


import com.example.demo.param.ResultVO;

public class ResultVOUtil {

    public static ResultVO success(Object object) {
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(object);
        resultVO.setCode(1);
        resultVO.setMsg("操作成功!");
        return resultVO;
    }

    public static ResultVO success() {
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(null);
        resultVO.setCode(1);
        resultVO.setMsg("操作成功!");
        return resultVO;
    }

    public static ResultVO fail(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(object);
        resultVO.setCode(2);
        resultVO.setMsg("操作失败!");
        return resultVO;
    }

    public static ResultVO failMsg(String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(null);
        resultVO.setCode(2);
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static ResultVO fail(){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(null);
        resultVO.setCode(2);
        resultVO.setMsg("操作失败!");
        return resultVO;
    }

    public static ResultVO fail(Object object, String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(object);
        resultVO.setCode(2);
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static ResultVO noData(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(object);
        resultVO.setCode(0);
        resultVO.setMsg("没有查询到数据!");
        return resultVO;
    }

    public static ResultVO noData(){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(null);
        resultVO.setCode(0);
        resultVO.setMsg("没有查询到数据!");
        return resultVO;
    }

    public static ResultVO abnormal(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(object);
        resultVO.setCode(-2);
        resultVO.setMsg("服务器异常!");
        return resultVO;
    }

    public static ResultVO abnormal(){
        ResultVO resultVO = new ResultVO();
        resultVO.setDatas(null);
        resultVO.setCode(-2);
        resultVO.setMsg("服务器异常!");
        return resultVO;
    }
}
