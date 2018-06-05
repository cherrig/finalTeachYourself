package project.teachyourself.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model object that contains every field used to interact with the api
 */
public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("age")
    private int age;

    @SerializedName("score")
    private int score;

    @SerializedName("image")
    private String image;

    @SerializedName("admin")
    private boolean admin;

    /**
     * Empty constructor
     */
    public User() {
        this.id = 0;
        this.name= "";
        this.email = "";
        this.password = "";
        this.age = 0;
        this.score = 0;
        this.image = "";
    }

    /**
     * Constructor
     * @param id id
     * @param name name
     * @param email email
     * @param password password
     * @param age age
     */
    public User(int id, String name, String email, String password, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
    }

    /**
     * Constructor
     * @param id id
     * @param name name
     * @param email email
     * @param password password
     * @param age age
     * @param score score
     */
    public User(int id, String name, String email, String password, int age, int score) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.score = score;
    }

    /**
     * Get id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get age
     * @return age
     */
    public int getAge() {
        return age;
    }

    /**
     * Set age
     * @param age age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Get profile picture
     * @return profile picture
     */
    public String getImage() {
        return image;
    }

    /**
     * Set profile picture
     * @param image profile picture
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get score total in every category
     * @return score total in every category
     */
    public int getScore() {
        return score;
    }

    /**
     * Set score total in every category
     * @param score score total in every category
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Is the user an admin
     * @return boolean
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Set if the user is an admin
     * @param admin boolean
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
