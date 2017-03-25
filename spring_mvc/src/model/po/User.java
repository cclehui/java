package model.po;

public class User {
    
    private int id;
    private String name;
    private String birthday;
    private int sex;
    
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    
    
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", birthday=" + birthday
                + ", sex=" + sex + "]";
    }
    
    

}
