package jereme.urban_network_dating.List;

public class FriendList {
    private String name;
    private String id;
    private String photo;
    private String status;

    public FriendList() {
        this.name = name;
        this.id = id;
        this.photo = photo;
        this.status = status;
    }

    public FriendList(String name, String id, String photo, String status) {
        this.name = name;
        this.id = id;
        this.photo = photo;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getStatus() {
        return status;
    }
}
