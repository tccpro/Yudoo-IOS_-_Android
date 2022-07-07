package jereme.urban_network_dating.List;


public class MessageFriendList {
    private String title;
    private String message;
    private String photo;
    private String messagedirection;
    private String user;
    private String user4;
    private String partner;
    private String date;

    public MessageFriendList() {
        this.title = title;
        this.message = message;
        this.photo = photo;
        this.messagedirection = messagedirection;
        this.user = user;
        this.user4 = user4;
        this.partner = partner;
        this.date = date;
    }

    public MessageFriendList(String zonetitle, String address, String photo, String messagedirection, String user, String user4, String partner, String date) {
        this.title = zonetitle;
        this.message = address;
        this.photo = photo;
        this.messagedirection = messagedirection;
        this.user = user;
        this.user4 = user4;
        this.partner = partner;
        this.date = date;
    }

    public String getMessageTitle() {
        return title;
    }

    public String getMessageDescription() {return message; }

    public String getPhoto() {
        return photo;
    }

    public String getMessagedirection() {
        return messagedirection;
    }

    public String getUser() {
        return user;
    }

    public String getUser4() {
        return user4;
    }

    public String getPartner() {
        return partner;
    }

    public String getDate() {return date; }
}