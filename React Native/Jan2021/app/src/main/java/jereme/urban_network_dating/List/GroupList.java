package jereme.urban_network_dating.List;

public class GroupList {
    private String zonetitle;
    private String address;
    private String photo;
    private String id;
   private  String status;
    private  String likecount;
    private  String commentcount;

    public GroupList() {
        this.zonetitle = zonetitle;
        this.address = address;
        this.photo = photo;
        this.id = id;
        this.status=status;
        this.likecount=likecount;
    }

    public GroupList(String zonetitle, String address, String photo, String id,String status) {
        this.zonetitle = zonetitle;
        this.address = address;
        this.photo = photo;
        this.id = id;
        this.status=status;

    }

    public String getZoneTitle() {
        return zonetitle;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoto() {
        return photo;
    }

    public String getID()
    {
        return id;
    }

    public String getStatus() {
        return status;
    }

}
