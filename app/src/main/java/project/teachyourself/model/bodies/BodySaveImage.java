package project.teachyourself.model.bodies;

public class BodySaveImage {
    private String email;
    private String image;

    public BodySaveImage(String email, String image) {
        this.email = email;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
