package lecture.mobile.final_project.ma01_20160940;

import java.io.Serializable;
//img 아니라 이름 바꿔야함..firstimage로
//스키장dto
public class SkiItem implements Serializable {
    private long _id;
    private String title;
    private String address;
    private String tel;
    private String mapX;
    private String mapY;
    private String contentid;
    private String overview;


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMapX() {
        return mapX;
    }

    public void setMapX(String mapX) {
        this.mapX = mapX;
    }

    public String getMapY() {
        return mapY;
    }

    public void setMapY(String mapY) {
        this.mapY = mapY;
    }

    public String getContentid() { return contentid; }

    public void setContentid(String contentid) { this.contentid = contentid; }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    @Override
    public String toString() {
        return "SkyItem{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
