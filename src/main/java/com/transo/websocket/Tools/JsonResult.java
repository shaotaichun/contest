package com.transo.websocket.Tools;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JsonResult implements Serializable {
    private static final long serialVersionUID = -4800793124936904868L;
    @ApiModelProperty(value = "状态码",required = true)
    private  int code;
    @ApiModelProperty(value = "返回提示信息",required = true)
    private  String msg = "";
    @ApiModelProperty(value = "返回数据的行数",required = true)
    private  int count;
    @ApiModelProperty(value = "返回的数据",required = true)
    private  Object data;
    @ApiModelProperty(value = "状态，成功-失败",required = true)
    private String status;
    public JsonResult(int count, Object data) {
        this.code = 200;
        this.count = count;
        this.data = data;
        this.status = "SUCCESS";
    }
    public JsonResult(String msg) {
        this.msg = msg;
        this.status = "FAIL";
    }
    public JsonResult() {
    }
    public JsonResult(Object data, int code) {
        this.data = data;
        this.status = "SUCCESS";
        this.code = 200;
    }
    public JsonResult(int code) {
        this.status = "SUCCESS";
        this.code = 200;
    }
    @Override
    public String toString() {
        return "JsonResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", count=" + count +
                ", data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}
