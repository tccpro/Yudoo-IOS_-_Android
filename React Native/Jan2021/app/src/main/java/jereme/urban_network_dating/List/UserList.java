package jereme.urban_network_dating.List;

public class UserList {
    private String name;
    private String id;
    private String photo;

    public UserList() {
        this.name = name;
        this.id = id;
        this.photo = photo;
    }

    public UserList(String name, String id, String photo) {
        this.name = name;
        this.id = id;
        this.photo = photo;
    }

    public String getZoneTitle() {
        return name;
    }

    public String getAddress() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }
}
