package cn.edu.sicnu.cs.pojo;

import cn.edu.sicnu.cs.model.Versionhistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class VersionListPojo {

    String vbigversion;

    List<Versionhistory> children;

}
