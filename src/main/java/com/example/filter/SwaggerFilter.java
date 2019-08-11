package com.example.filter;

import com.example.util.ResponseWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SwaggerFilter implements Filter {

    private static JSONObject tokenDefinition;
    private static JSONObject resultTokenDefinition;
    private static JSONObject tokenTag;
    private static JSONObject loginPath;
    private static JSONObject logoutPath;

    // 拦截api信息，动态添加认证接口
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response); // 转换成代理类
        // 这里只拦截返回，直接让请求过去，如果在请求前有处理，可以在这里处理
        chain.doFilter(request, responseWrapper);
        byte[] content = responseWrapper.getContent(); // 获取返回值
        // 判断是否有值
        if (content.length>0) {
            String str = new String(content, StandardCharsets.UTF_8);
            JSONObject swagger = JSONObject.fromObject(str);
            JSONObject definitions = swagger.getJSONObject("definitions");
            definitions.put("令牌", tokenDefinition);
            definitions.put("返回结果«令牌»", resultTokenDefinition);
            JSONArray tags = swagger.getJSONArray("tags");
            tags.add(tokenTag);
            JSONObject paths = swagger.getJSONObject("paths");
            paths.put("/login", loginPath);
            paths.put("/logout", logoutPath);
            // 把返回值输出到客户端
            ServletOutputStream out = response.getOutputStream();
            response.setContentLength(-1);
            out.write(swagger.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }

    static {
        tokenDefinition = JSONObject.fromObject(
                        "{"+
                        "    \"type\": \"object\","+
                        "    \"title\": \"令牌\","+
                        "    \"properties\": {"+
                        "        \"token\": {"+
                        "            \"allowEmptyValue\": false,"+
                        "            \"description\": \"令牌\","+
                        "            \"type\": \"string\""+
                        "        },\n"+
                        "        \"loginTime\": {"+
                        "            \"allowEmptyValue\": false,"+
                        "            \"description\": \"登录时间戳\","+
                        "            \"format\": \"int64\","+
                        "            \"type\": \"integer\""+
                        "        }"+
                        "    }"+
                        "}");
        resultTokenDefinition = JSONObject.fromObject(
                        "{"+
                        "    \"title\": \"返回结果«令牌»\","+
                        "    \"type\": \"object\","+
                        "    \"properties\": {"+
                        "        \"code\": {"+
                        "            \"type\": \"integer\","+
                        "            \"format\": \"int32\","+
                        "            \"description\": \"状态码\","+
                        "            \"allowEmptyValue\": false"+
                        "        },"+
                        "        \"data\": {"+
                        "            \"description\": \"数据\","+
                        "            \"allowEmptyValue\": false,"+
                        "            \"$ref\": \"#/definitions/令牌\""+
                        "        },"+
                        "        \"message\": {"+
                        "            \"type\": \"string\","+
                        "            \"description\": \"消息\","+
                        "            \"allowEmptyValue\": false"+
                        "        }"+
                        "    }"+
                        "}");
        tokenTag = JSONObject.fromObject(
                        "{"+
                        "    \"description\": \"Token\","+
                        "    \"name\": \"令牌\""+
                        "}");
        loginPath = JSONObject.fromObject(
                        "{"+
                        "    \"post\": {"+
                        "        \"tags\": [\"令牌\"],"+
                        "        \"summary\": \"登录\","+
                        "        \"operationId\": \"loginUsingPOST\","+
                        "        \"produces\": [\"application/json;charset=UTF-8\"],"+
                        "        \"responses\": {"+
                        "            \"200\": {"+
                        "                \"description\": \"OK\","+
                        "                \"schema\": {"+
                        "                    \"$ref\": \"#/definitions/返回结果«令牌»\""+
                        "                }"+
                        "            },"+
                        "            \"401\": {"+
                        "                \"description\": \"Unauthorized\""+
                        "            },"+
                        "            \"403\": {"+
                        "                \"description\": \"Forbidden\""+
                        "            },"+
                        "            \"404\": {"+
                        "                \"description\": \"Not Found\""+
                        "            }"+
                        "        },"+
                        "        \"parameters\": [{"+
                        "            \"name\": \"username\","+
                        "            \"in\": \"query\","+
                        "            \"description\": \"用户名\","+
                        "            \"required\": true,"+
                        "            \"type\": \"string\""+
                        "        },"+
                        "        {"+
                        "            \"name\": \"password\","+
                        "            \"in\": \"query\","+
                        "            \"description\": \"密码\","+
                        "            \"required\": true,"+
                        "            \"type\": \"string\""+
                        "        }]"+
                        "    }"+
                        "}");
        logoutPath = JSONObject.fromObject(
                        "{"+
                        "    \"get\": {"+
                        "        \"tags\": [\"令牌\"],"+
                        "        \"summary\": \"登出\","+
                        "        \"operationId\": \"logoutUsingGET\","+
                        "        \"produces\": [\"application/json;charset=UTF-8\"],"+
                        "        \"parameters\": [{"+
                        "            \"name\": \"token\","+
                        "            \"in\": \"header\","+
                        "            \"description\": \"认证参数\","+
                        "            \"required\": true,"+
                        "            \"type\": \"string\""+
                        "        }],"+
                        "        \"responses\": {"+
                        "            \"200\": {"+
                        "                \"description\": \"OK\","+
                        "                \"schema\": {"+
                        "                    \"$ref\": \"#/definitions/返回结果\""+
                        "                }"+
                        "            },"+
                        "            \"401\": {"+
                        "                \"description\": \"Unauthorized\""+
                        "            },"+
                        "            \"403\": {"+
                        "                \"description\": \"Forbidden\""+
                        "            },"+
                        "            \"404\": {"+
                        "                \"description\": \"Not Found\""+
                        "            }"+
                        "        }"+
                        "    }"+
                        "}");
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

}
