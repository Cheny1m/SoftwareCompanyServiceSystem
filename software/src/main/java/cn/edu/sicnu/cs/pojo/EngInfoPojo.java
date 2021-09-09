package cn.edu.sicnu.cs.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EngInfoPojo {

    private Integer uid;

    private String username;

    private String urealname;

    private int urole_id;

    private String rdesc;

    private String ugender;

    private String uemail;

    private String umobile;

    private Date ujoin;
}
