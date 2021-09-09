package cn.edu.sicnu.cs.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberpojo {

    private String fzname;

    private Integer uid;

    private Integer tedays;

    private Float tehours;
}
