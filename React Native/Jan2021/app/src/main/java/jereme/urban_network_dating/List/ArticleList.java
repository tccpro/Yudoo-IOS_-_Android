package jereme.urban_network_dating.List;

public class ArticleList {
    private String title;
    private String message;
    private String date;
    private String user;
    private String file;
    private String userimage;
    private String id;
    private String likecount;
    private String commentcount;
    private  int like;
    public ArticleList() {
        this.title = title;
        this.message = message;
        this.date = date;
        this.user = user;
        this.file = file;
        this.userimage=userimage;
        this.id=id;
        this.likecount=likecount;
        this.commentcount=commentcount;
        this.like =like;
    }

    public ArticleList(String title, String message, String date, String user, String file,String userimage,String id,String likecount,String commentcount,int like) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.user = user;
        this.file = file;
        this.userimage=userimage;
        this.id=id;
        this.likecount=likecount;
        this.commentcount=commentcount;
        this.like =like;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getUserimage() {
        return userimage;
    }

    public String getUser()
    {
        return user;
    }

    public String getFile()
    {
        return file;
    }

    public String getId()
    {
        return id;
    }
    public String getLikecount()
    {
        return likecount;
    }
    public String getCommendCount()
    {
        return commentcount;
    }

    public void sertComment(String commentcount)
    {
        this.commentcount= commentcount;
    }
    public int getLike()
    {
        return like;
    }
    public void setLike(int like){
        this.like=like;
    }
}
