package lecture.mobile.final_project.ma01_20160940;

public class LikeListDto {
        private long _id;
        private String title;
        private String address;
        private String number;

        public LikeListDto ( String title, String address, String number) {
            this.title = title;
            this.address = address;
            this.number = number;
        }
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

