package jereme.urban_network_dating.List;

public class TasklistList {
    private String name;
    private String private_state;
    private String id;

    public TasklistList() {
        this.name = name;
        this.private_state = private_state;
        this.id = id;

    }

    public TasklistList(String name, String private_state, String id) {
        this.name = name;
        this.private_state = private_state;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public String getPrivate_state() {
        return private_state;
    }

    public String getId() {return id;}

}
