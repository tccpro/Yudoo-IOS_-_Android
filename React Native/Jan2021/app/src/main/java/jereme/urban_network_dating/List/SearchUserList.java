package jereme.urban_network_dating.List;

public class SearchUserList {
    private String name;
    private String id;
    private String photo;
    private String searchname;
    private String age;
    private String gender;
    private String birthday;
    private String hometown;
    private String current_city;
    private String looking;
    private String interest;
    private String morepic;
    private String aboutme;
    public SearchUserList() {
        this.name = name;
        this.id = id;
        this.photo = photo;
        this.searchname = searchname;
    }

    public SearchUserList(String name, String id, String photo, String searchname, String age, String gender, String birthday, String hometown, String currenty_city,String looking,String interest,String morepic,String aboutme) {
        this.name = name;
        this.id = id;
        this.photo = photo;
        this.searchname = searchname;
        this.age = age;
        this.gender = gender;
        this.birthday = birthday;
        this.hometown = hometown;
        this.current_city = currenty_city;
        this.looking = looking;
        this.interest = interest;
        this.morepic = morepic;
        this.aboutme=aboutme;
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

    public String getSearchName() {
        return searchname;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getHometown() {
        return hometown;
    }

    public String getCurrentyCity() {
        return current_city;
    }

    public String getInterest() {
        return interest;
    }

    public String getLooking() {
        return looking;
    }

    public String getMorepic() {
        return morepic;
    }
    public String getAboutme() {
        return aboutme;
    }

}
