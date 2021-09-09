package cn.edu.sicnu.cs.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInTeam {

    private String username;

    private String urealname;

    private String ujoin;

    private String userrole;

    private Integer tedays;

    private Float tehours;

    private Integer teestimate;


}
