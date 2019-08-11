package com.example.util;

import com.alibaba.fastjson.JSON;
import com.example.model.vo.ResultVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseUtil {

    public static void println(HttpServletResponse response, ResultVO resultVO) {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(resultVO.getCode());
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out!=null) {
            PrintWriter writer = new PrintWriter(out, true);
            writer.println(JSON.toJSONString(resultVO));
        }
    }

}
