package jereme.urban_network_dating.List;

public class InterestList {

    private String text;
    private boolean isSelected = false;

    public InterestList(String text,boolean isSelected) {
        this.text = text;
        this.isSelected =isSelected;
    }

    public String getText() {
        return text;
    }

    public void setText(String text){
        this.text =text;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
