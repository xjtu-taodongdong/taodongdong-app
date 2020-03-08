public class ProductItem {
    private String description;
    private int image;


    ProductItem(String description, int image) {
        this.description = description;
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }
}
