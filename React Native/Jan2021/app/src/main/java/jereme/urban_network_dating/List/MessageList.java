package jereme.urban_network_dating.List;

import java.io.Serializable;

public class MessageList implements Serializable {
    private String zonetitle;
    private String address;
    private String photo;

    public MessageList(String listitem) {
        this.zonetitle = zonetitle;
        this.address = address;
        this.photo = photo;
    }

    public MessageList(String zonetitle, String address, String photo) {
        this.zonetitle = zonetitle;
        this.address = address;
        this.photo = photo;
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
}

