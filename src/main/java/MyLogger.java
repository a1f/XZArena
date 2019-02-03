import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MyLogger {
    PrintWriter writer;
    // TODO: replace with something better
    final static String LOG_FILE = "/Users/alf/log.txt";

    private static MyLogger myLogger = null;

    public static MyLogger getInstance() {
        if (myLogger == null) {
            myLogger = new MyLogger();
        }
        return myLogger;
    }

    private MyLogger() {
        try {
            writer = new PrintWriter(LOG_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(String s) {
        writer.println(s);
        writer.flush();
    }
}