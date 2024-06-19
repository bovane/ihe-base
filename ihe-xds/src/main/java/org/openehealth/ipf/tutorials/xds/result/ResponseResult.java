package org.openehealth.ipf.tutorials.xds.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author zhibao
 * @version 1.0
 * @project aggregation-mediway-boot
 * @description 接口返回格式管理
 * @date 2023/12/21 16:03:33
 */

@ToString
@Data
public class ResponseResult<T> implements Serializable {
    /**
     * 状态代码
     */
    private long code;
    /**
     * 状态消息
     */
    private String msg;
    /**
     * 数量
     */
    @JsonIgnore
    private String sub_code;

    /*
     *
     */
    @JsonIgnore
    private String sub_msg;

    /**
     * 结果集分组
     */
   
    private Object data;

    // 构造器开始

    /**
     * 无参构造器(构造器私有，外部不可以直接创建)
     */
    private ResponseResult() {
        this.code = 0;
        this.msg = "成功";
    }

    /**
     * 有参构造器
     *
     * @param obj
     */
    private ResponseResult(T obj) {
        this.code = 0;
        this.data = obj;
        this.msg = "成功";
    }

    /**
     * 有参构造器
     *
     * @param code
     * @param msg
     */
    private ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通用返回成功（没有返回结果）
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult();
    }

    /**
     * 返回成功（有返回结果）
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>(data);
    }

    /**
     * 通用返回失败
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> ResponseResult<T> failure(Integer code, String msg) {
        return new ResponseResult<T>(code, msg);
    }
}
