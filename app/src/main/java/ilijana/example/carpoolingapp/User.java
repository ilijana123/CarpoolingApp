package ilijana.example.carpoolingapp;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String userType;

    public User(int id, String email, String password, String name, String surname, String userType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUserType() {
        return userType;
    }
}
