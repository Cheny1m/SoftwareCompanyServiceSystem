package cn.edu.sicnu.cs.model;

import java.util.Date;

public class Versionhistory {
    private Integer vid;

    private String vbigversion;

    private String vversion;

    private Date vtime;

    private String vvalue;

    private String vtitle;

    private String vdesc;

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }

    public String getVbigversion() {
        return vbigversion;
    }

    public void setVbigversion(String vbigversion) {
        this.vbigversion = vbigversion == null ? null : vbigversion.trim();
    }

    public String getVversion() {
        return vversion;
    }

    public void setVversion(String vversion) {
        this.vversion = vversion == null ? null : vversion.trim();
    }

    public Date getVtime() {
        return vtime;
    }

    public void setVtime(Date vtime) {
        this.vtime = vtime;
    }

    public String getVvalue() {
        return vvalue;
    }

    public void setVvalue(String vvalue) {
        this.vvalue = vvalue == null ? null : vvalue.trim();
    }

    public String getVtitle() {
        return vtitle;
    }

    public void setVtitle(String vtitle) {
        this.vtitle = vtitle == null ? null : vtitle.trim();
    }

    public String getVdesc() {
        return vdesc;
    }

    public void setVdesc(String vdesc) {
        this.vdesc = vdesc == null ? null : vdesc.trim();
    }

    @Override
    public String toString() {
        return "Versionhistory{" +
                "vid=" + vid +
                ", vbigversion='" + vbigversion + '\'' +
                ", vversion='" + vversion + '\'' +
                ", vtime=" + vtime +
                ", vvalue='" + vvalue + '\'' +
                ", vtitle='" + vtitle + '\'' +
                ", vdesc='" + vdesc + '\'' +
                '}';
    }
}