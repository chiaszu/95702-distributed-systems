// author: Chia-Szu, Kuo (chiaszuk)

package cmu.edu.recipeideas;

public class Recipe {
    private String id;
    private String name;
    private String imageUrl;

    public Recipe(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}