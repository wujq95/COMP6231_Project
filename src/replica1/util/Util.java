package replica1.util;

import replica1.as.AsGameServer;
import replica1.eu.EuGameServer;
import replica1.na.NaGameServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Util {

    /**
     * check if user name exists
     * @param userName
     * @return
     */
    public boolean checkUserName(String userName,String ipAddress){
        Character c = userName.charAt(0);

        ConcurrentHashMap<Character, List<String>> map;

        if(ipAddress.startsWith("182")){
            map = AsGameServer.asiaMap;
        }else if(ipAddress.startsWith("132")){
            map = NaGameServer.northAmericaMap;
        }else{
            map = EuGameServer.europeMap;
        }

        if(!map.containsKey(c)){
            return false;
        }else{
            List<String> list = map.get(c);
            for(String str:list){

                if(str.split("\\|")[0].equalsIgnoreCase(userName)) return true;
            }
        }
        return false;
    }

    /**
     * check if the password is right
     * @param userName
     * @param password
     * @return
     */
    public boolean checkPassword(String userName, String password,String ipAddress){
        List<String> list;
        if(ipAddress.startsWith("182")){
            list = AsGameServer.asiaMap.get(userName.charAt(0));
        }else if(ipAddress.startsWith("132")){
            list = NaGameServer.northAmericaMap.get(userName.charAt(0));
        }else{
            list = EuGameServer.europeMap.get(userName.charAt(0));
        }

        for(String str:list){
            String strName = str.split("\\|")[0];
            String strPwd = str.split("\\|")[4];
            if(strName.equalsIgnoreCase(userName)&&strPwd.equals(password)){
                return true;
            }
        }
        return false;
    }

    /**
     * check if ip address is in the right pattern
     * @param ipAddress
     * @return
     */
    public static boolean checkAddress(String ipAddress) {
        String regex = "\\d{3}\\.\\d{3}\\.\\d{3}";
        Pattern pattern = Pattern.compile(regex);
        if (ipAddress.startsWith("182.")) {
            if (pattern.matcher(ipAddress.substring(4)).matches()) {
                return true;
            }
        } else if (ipAddress.startsWith("93.")) {
            if (pattern.matcher(ipAddress.substring(3)).matches()) {
                return true;
            }
        } else if (ipAddress.startsWith("132.")) {
            if (pattern.matcher(ipAddress.substring(4)).matches()) {
                return true;
            }
        }
        return false;
    }


    /**
     * remove one item from a list of items
     * @param list
     * @param name
     * @return
     */
    public List removeItemFromList(List<String> list,String name){
        for(int i = list.size() - 1; i >= 0; i--){
            String item = list.get(i);
            if(item.startsWith(name+"|")){
                list.remove(item);
            }
        }
        return list;
    }


    /**
     * find the player information by user name
     * @param userName
     * @return
     */
    public String findPlayer(String userName, String ipAddress){
        List<String> list;
        if(ipAddress.startsWith("132")){
            list  = NaGameServer.northAmericaMap.get(userName.charAt(0));
        }else if(ipAddress.startsWith("182")){
            list = AsGameServer.asiaMap.get(userName.charAt(0));
        }else{
            list = EuGameServer.europeMap.get(userName.charAt(0));
        }
        String res = null;
        for(String str:list){
            if(str.startsWith(userName+"|")){
                res = str;
            }
        }
        return res;
    }

    /**
     * check if the name has been in the account.txt
     * @param path
     * @param str
     * @return
     * @throws Exception
     */
    public boolean checkNameExist(String path,String str) throws Exception{
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String string;
        while((string=bufferedReader.readLine())!=null){
            if(string.startsWith(str+"|")) return true;
        }
        return false;
    }


}
