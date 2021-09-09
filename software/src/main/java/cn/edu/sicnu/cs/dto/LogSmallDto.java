package cn.edu.sicnu.cs.dto;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class LogSmallDto implements Serializable {
    // 描述
    private String description;

    // 请求ip
    private String requestIp;

    // 请求耗时
    private Long time;

    // 方法名
    private String method;

    // 参数
    private String params;
    /**
     * 异常细节
     */
    private byte[] exceptionDetail;

    private String address;

    private String browser;

    // 创建日期
    private Timestamp createTime;

    private String username;

    private String rolename;
}
