package mc.bape.vapu;

import mc.bape.utils.Helper;

public class Debug {
    public static void sendMessage(String Message){
        if(Client.ENABLE_DEBUG){
            Helper.sendMessage(Message);
        }
    }
}
