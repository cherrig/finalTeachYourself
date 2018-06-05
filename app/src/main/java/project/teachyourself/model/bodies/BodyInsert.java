package project.teachyourself.model.bodies;

public class BodyInsert {
    private String email;
    private String name;
    private int age;
    private String password;
    private boolean admin;

    public BodyInsert(String email, String name, int age, String password, boolean admin) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.password = password;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}