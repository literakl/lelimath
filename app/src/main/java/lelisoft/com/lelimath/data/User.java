package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Current user
 * Created by Leoš on 29.04.2016.
 */
public class User {
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String nickname;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", id=" + id +
                '}';
    }
}
