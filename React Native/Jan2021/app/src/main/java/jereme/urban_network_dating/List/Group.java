package jereme.urban_network_dating.List;

public class Group {
    private String title, genre;
    private int image ;
    public Group() {
    }
    public Group(String title, String genre, int image) {
        this.title = title;
        this.genre = genre;
        this.image = image;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String name) {
        this.title = name;
    }
    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
}