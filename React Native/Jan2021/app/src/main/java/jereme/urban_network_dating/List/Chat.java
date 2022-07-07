package jereme.urban_network_dating.List;
public class Chat {

    private String message, name;
    private int image ;
    public Chat() {
    }
    public Chat(String message, String name, int image) {
        this.message = message;
        this.name = name;
        this.image = image;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }
    public void setName(String time) {
        this.name = time;
    }
}

