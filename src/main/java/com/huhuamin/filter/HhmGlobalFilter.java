package com.huhuamin.filter;

import com.alibaba.fastjson.JSON;
import com.huhuamin.req.ReqHeader;
import com.huhuamin.result.JsonResult;
import com.huhuamin.service.ICheckRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/14 20:55
 * @Description:
 */
@Component
@Slf4j
public class HhmGlobalFilter implements GlobalFilter, Ordered {


    /**
     * 参数相关校验
     */

    private final ICheckRange checkRange;


    public HhmGlobalFilter(ICheckRange checkRange) {
        this.checkRange = checkRange;
    }


    /**
     * 全局过滤器
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String info = String.format("************全局过滤器%s************", new Date());
        log.debug(info);
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //放行
        if (request.getURI().getPath().contains("/spec/")) {
            return chain.filter(exchange);
        }

//            | Header | Vaule | Remark |
//            | --- | --- | --- |
//            | X-Request-AppId |001 | 必填 开发系统是分配账号 |
//            | X-Request-AppSecret |0xxx3| 必填 开发系统是分配秘钥 |
//            | X-Request-deviceType | 1 |必填 [1-微信 2-WAP 3-Android 4-iOS 5-pc 6-手动创建 7-小程序] |
//            | X-Request-DeviceId | 342223 | 选填 设备id；3，4必填，即安卓和ios 的设备id |
//            | X-Request-Token | xx | 选填（/anonymous/可以必填）(/option/可填可不填) (其他场景是必传的) |

        JsonResult jsonResult = new JsonResult(false);
        ReqHeader reqHeader = new ReqHeader();
        //设备类型证书判断
        String deviceType = request.getHeaders().getFirst("X-Request-deviceType");
        if (StringUtils.isEmpty(deviceType)) {
            jsonResult.setMessage("X-Request-deviceType 不能为空");
            return errorReturn(jsonResult, response);
        }
        boolean result = StringUtils.isNumeric(deviceType);
        if (!result) {
            jsonResult.setMessage("X-Request-deviceType 类型不正确");
            return errorReturn(jsonResult, response);
        }

        convertHttpHeaderToReqHeader(request, reqHeader);
        result = checkRange.check(reqHeader, jsonResult);
        if (!result) {
            return errorReturn(jsonResult, response);
        }


//        if (!result) {
//            jsonResult.setMessage("");
//        }


        return chain.filter(exchange);
    }

    private void convertHttpHeaderToReqHeader(ServerHttpRequest request, ReqHeader reqHeader) {
        reqHeader.setUri(request.getURI().getPath());
        reqHeader.setAppId(request.getHeaders().getFirst("X-Request-AppId"));
        reqHeader.setAppSecret(request.getHeaders().getFirst("X-Request-AppSecret"));
        reqHeader.setDeviceType(Byte.valueOf(request.getHeaders().getFirst("X-Request-deviceType")));
        reqHeader.setDeviceId(request.getHeaders().getFirst("X-Request-DeviceId"));
        reqHeader.setToken(request.getHeaders().getFirst("X-Request-Token"));
    }

    /**
     * 通用错误范围json，及描述信息
     *
     * @param jsonResult
     * @param response
     * @return
     */
    private Mono<Void> errorReturn(JsonResult jsonResult, ServerHttpResponse response) {
        byte[] bits = JSON.toJSONString(jsonResult).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
