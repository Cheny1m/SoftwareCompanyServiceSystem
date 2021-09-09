package cn.edu.sicnu.cs.model;

public class Front_dic_item {
    private Integer fdiId;

    private Integer fdiTypeId;

    private String fdiName;

    public Integer getFdiId() {
        return fdiId;
    }

    public void setFdiId(Integer fdiId) {
        this.fdiId = fdiId;
    }

    public Integer getFdiTypeId() {
        return fdiTypeId;
    }

    public void setFdiTypeId(Integer fdiTypeId) {
        this.fdiTypeId = fdiTypeId;
    }

    public String getFdiName() {
        return fdiName;
    }

    public void setFdiName(String fdiName) {
        this.fdiName = fdiName == null ? null : fdiName.trim();
    }
}