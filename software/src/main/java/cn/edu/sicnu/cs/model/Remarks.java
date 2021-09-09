package cn.edu.sicnu.cs.model;

public class Remarks {
    private Long reid;

    private String retype;

    private Long reaffairtId;

    private String restate;

    private String recontent;

    public Long getReid() {
        return reid;
    }

    public void setReid(Long reid) {
        this.reid = reid;
    }

    public String getRetype() {
        return retype;
    }

    public void setRetype(String retype) {
        this.retype = retype == null ? null : retype.trim();
    }

    public Long getReaffairtId() {
        return reaffairtId;
    }

    public void setReaffairtId(Long reaffairtId) {
        this.reaffairtId = reaffairtId;
    }

    public String getRestate() {
        return restate;
    }

    public void setRestate(String restate) {
        this.restate = restate == null ? null : restate.trim();
    }

    public String getRecontent() {
        return recontent;
    }

    public void setRecontent(String recontent) {
        this.recontent = recontent == null ? null : recontent.trim();
    }
}