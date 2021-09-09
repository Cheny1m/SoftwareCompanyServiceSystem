package cn.edu.sicnu.cs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Classname StatisticsFormKefuMg
 * @Description TODO
 * @Date 2020/12/16 10:04
 * @Created by Huan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsFormKefuMg {
    // 客服姓名
    private String name;
    // 上线时间
    private String loginTime;
    // 处理表单数
    private int handleUserFormNuMm;
    // 提交工单数
    private int commitOrderNumber;
    // 提交工单比例
    private float ratio;
    // 在线时长
    private String onLoginTime;


}
