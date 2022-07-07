package jereme.urban_network_dating.List;

public class ImageList {


        private String photo;
        private boolean isSelected = false;

        public ImageList(String photo) {
            this.photo = photo;
        }

        public String getText() {
            return photo;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }


        public boolean isSelected() {
            return isSelected;
        }


}
