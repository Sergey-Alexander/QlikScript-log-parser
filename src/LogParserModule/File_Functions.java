package LogParserModule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class File_Functions {
    public static void set_delimiter(String new_delimiter) {
        File_Functions.delimiter = new_delimiter;
    }

    static private String delimiter = "|";

    /**
     * Write lists of strings to file that are passed to this function
     * @param output_full_path - full path of file to write to
     * @param chunk - list of strings to write
     * @return - 0 if correct, -1 if error occured
     */
    public int write_to_file(String output_full_path, List<String> chunk)
    {
        // Open stream for output
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output_full_path, true)))
        {
            for (String line:chunk)
            {
                writer.write(line);
                writer.newLine();
            }
        }
        catch (Exception e)
        {
            Outputter.print_error(e.getMessage());
            return 1;
        }
        return 0;
    }

    // TODO incorporate debug into user friendly switch
    /**
     *
     * @param master_file_full_path - full path to master file that data will be appended to
     * @param source_file_name - file name of source log
     * @param data_list - List of lines that needs to be written to file
     * @param debug - debug flag, mainly for writing line separator between files
     * @return - 0 if function succeeded, -1 if exception caught
     */
    public int write_file_data_to_master_file(String master_file_full_path,
                                       String source_file_name,
                                       List<ArrayList<String>> data_list,
                                       boolean debug)
    {
        int start_index = debug ? 0 : 1;
        // Open master file for appending
        try (BufferedWriter csv_stream = new BufferedWriter(new FileWriter(master_file_full_path, true)))
        {
            for(int i = start_index; i < data_list.size(); i++)
            {
                String new_line = source_file_name + delimiter;
                String joined_data = String.join(delimiter, data_list.get(i));
                new_line = new_line+joined_data;
                Outputter.print_info(new_line);
                csv_stream.write(new_line);
                csv_stream.newLine();
            }

            // Visual separator line between files
            if(debug)
            {
                csv_stream.newLine();
            }
        }
        catch (Exception e)
        {
            Outputter.print_error(e.getMessage());
            return 1;
        }

        return 0;
    }

    /**
     * Append single line to master file
     * @param master_file_full_path - full path to destination master file
     * @param string - string to append
     * @return - 0 if function succeeded, -1 if exception caught
     */
    public int write_line_to_master_file(String master_file_full_path, String string)
    {
        try (BufferedWriter csv_stream = new BufferedWriter(new FileWriter(master_file_full_path, true)))
        {
            csv_stream.write(string);
            csv_stream.newLine();
        }
        catch (Exception e)
        {
            Outputter.print_error(e.getMessage());
            return 1;
        }

        return 0;
    }

}
