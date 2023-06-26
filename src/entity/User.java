package entity;

import javax.swing.*;
import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private ImageIcon profilePicture;
    private static final Long serialVersionUID = 2906642554793891381L;


    public User(String name, ImageIcon profilePicture) {

        this.name = name;
        this.profilePicture = profilePicture;
    }

    public boolean equals(Object obj) {
        if(obj!=null && obj instanceof User)
            return name.equals(((User)obj).getName());
        return false;
    }
    public int hashCode(){
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageIcon getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ImageIcon profilePicture) {
        this.profilePicture = profilePicture;
    }

    /*@Override
    public String toString() {
        return name;
    }*/
}
