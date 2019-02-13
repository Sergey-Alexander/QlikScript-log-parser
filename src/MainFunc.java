import LogParserModule.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MainFunc {
    public static void main(String[] args)
    {
        // Log files folder
        String files_path;

        String output_folder_path;

        String time_log_name;
        String master_log_name;


        files_path = prompt_user("Enter log files folder: ");
        output_folder_path = prompt_user("Enter output folder: ");
        time_log_name = prompt_user("Enter time log sub name: ");
        master_log_name = prompt_user("Enter master log sub name: ");

        parse_folder(files_path, output_folder_path, time_log_name, master_log_name);
    }


    /**
     * Prompt user for folder to parse, output folder and names for master files
     * @param message - message to invite user with
     * @return - return user input in string form
     */
    private static String prompt_user(String message)
    {
        String input;
        Scanner sc = new Scanner(System.in);
        Outputter.print_info(message);
        input = sc.nextLine();

        return input;
    }

    /**
     * Parse folder for log time and data contained between "SELECT" statement and "lines found" line
     * @param files_path - logs directory
     * @param output_folder_path - output destination folder
     * @param time_log_sub_name - sub name for time log file
     * @param master_sub_name - sub name for data master file
     */
    private static void parse_folder(String files_path, String output_folder_path, String time_log_sub_name, String master_sub_name)
    {
        // Create folder controller with input folder parameter
        Folder_Controller folder_controller = new Folder_Controller(output_folder_path);
        Log_Parser log_parser = new Log_Parser();

        // ==================================================================
        // Clear output directory
        folder_controller.clear_directory();
        // ==================================================================
        folder_controller.set_input_folder(files_path);
        folder_controller.parse_folder_into_chunks(output_folder_path, log_parser,
                Regex_Strings.select_pattern, Regex_Strings.lines_pattern);
        // Get log time of logs in folder
        // Change current folder

        // Parse folder log times
        folder_controller.parse_folder_log_time(
                output_folder_path + generate_master_file_name(time_log_sub_name),
                log_parser);



        folder_controller.set_input_folder(output_folder_path);

        // Create array of patterns to find in parsed chunks
        ArrayList<Pattern> patterns_to_find = new ArrayList<>();
        patterns_to_find.add(Regex_Strings.from_pattern);
        patterns_to_find.add(Regex_Strings.fields_pattern);
        patterns_to_find.add(Regex_Strings.lines_pattern);

        folder_controller.parse_chunks_folder_into_log_file(
                output_folder_path + generate_master_file_name(master_sub_name),
                log_parser,
                patterns_to_find);
    }

    /**
     * Get master log name containing delimiter, unix timestamp and user defined sub name
     * @param sub_name - user defined master log name
     * @return complete file name
     */
    private static String generate_master_file_name(String sub_name)
    {
        return "!" + System.currentTimeMillis() / 1000L + " " + sub_name + " master_file.log";
    }
}
