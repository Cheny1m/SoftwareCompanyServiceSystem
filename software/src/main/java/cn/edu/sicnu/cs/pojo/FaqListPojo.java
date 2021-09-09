package cn.edu.sicnu.cs.pojo;

import cn.edu.sicnu.cs.model.Faq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqListPojo {
    String qtype;
    List<Faq> children;

}
