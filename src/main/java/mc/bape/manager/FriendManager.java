package mc.bape.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FriendManager {
    private static HashMap<String, String> friends;

    public static boolean isFriend(String name) {
        return friends.containsKey(name);
    }

    public static String getAlias(Object friends2) {
        return friends.get(friends2);
    }

    public static HashMap<String, String> getFriends() {
        return friends;
    }

    public static void init() {
        friends = new HashMap<>();
        List<String> frriends = ConfigManager.read("Friends.cfg");
        Iterator<String> var3 = frriends.iterator();

        while (var3.hasNext()) {
            String v = (String) var3.next();
            if (v.contains(":")) {
                String name = v.split(":")[0];
                String alias = v.split(":")[1];
                friends.put(name, alias);
            } else {
                friends.put(v, v);
            }
        }
    }
}