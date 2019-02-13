package LogParserModule;

import LogParserModule.Logger.ConsoleLogger;
import LogParserModule.Logger.Logger;

import java.util.Collections;

public class Outputter {
    private static Logger logger = new ConsoleLogger();

    public static void print_info(String string)
    {
        logger.output(string);
    }

    public static void print_info(int i)
    {
        logger.output(Integer.toString(i));
    }

    public static void print_error(String error)
    {
        int n = 20;
        String frame_part = "=";
        String frame_horizontal = String.join("", Collections.nCopies(n, frame_part));
        String decorated_error = frame_horizontal + "\n" + error + "\n" + frame_horizontal;

        logger.output(decorated_error);
    }
}
