package replica2.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static String getFormattedDate() {
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    /**
     * server log
     * @param serverName
     * @param msg
     * @throws IOException
     */
    public static void serverLog(String serverName, String msg) throws IOException {
        String path = "src/replica2/logger/logs/serverLogs/"+serverName+"ServerLog.txt";
        File file = new File(path);
        if(file.exists()) {
            FileWriter fileWriter = new FileWriter(path, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("DATE: " + getFormattedDate() + " " + msg);
            printWriter.close();
        }
    }

    /**
     * player log
     * @param serverName
     * @param playerUserName
     * @param msg
     * @throws IOException
     */
    public static void playerLog(String serverName,String playerUserName, String msg) throws IOException {
        String path = "src/replica2/logger/logs/"+serverName+"PlayerLogs/"+playerUserName+".txt";
        File file = new File(path);
        if(file.exists()) {
            FileWriter fileWriter = new FileWriter(path, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("DATE: " + getFormattedDate() + " " + msg);
            printWriter.close();
        }
    }

    /**
     * admin log
     * @param msg
     * @throws IOException
     */
    public static void adminLog(String msg) throws IOException {
        String path = "src/replica2/logger/logs/adminLogs/adminLog.txt";
        File file = new File(path);
        if(file.exists()) {
            FileWriter fileWriter = new FileWriter(path, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("DATE: " + getFormattedDate() + " " + msg);
            printWriter.close();
        }
    }
}
