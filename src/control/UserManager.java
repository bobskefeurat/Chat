package control;

import entity.User;

import java.util.ArrayList;

public class UserManager {

    public UserManager() {

    }

    public ArrayList<String> createStringList(ArrayList<User> onlineUsers, User user) {

        ArrayList<String> usernames = new ArrayList<>();

        for (User onlineUser : onlineUsers) {
            if (!onlineUser.equals(user)) {

                usernames.add(onlineUser.getName());
            }
        }
        return usernames;
    }


}
