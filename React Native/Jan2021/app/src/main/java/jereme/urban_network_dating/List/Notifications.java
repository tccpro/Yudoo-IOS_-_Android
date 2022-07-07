package jereme.urban_network_dating.List;

public class Notifications {

    private String message, time,type;
    private String image ,user;

    public Notifications() {
    }
    public Notifications(String message, String time, String image,String type, String user) {
        this.message = message;
        this.time = time;
        this.image = image;
        this.type =type;
        this.user=user;
    }



    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
}

