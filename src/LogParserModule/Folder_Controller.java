package LogParserModule;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class Folder_Controller
{
    // Default delimiter
    private String delimiter = "|";

    /**
     * Getter for input_folder
     * @return path to input folder
     */
    public String get_input_folder()
    {
        return input_folder;
    }

    /**
     * Sets input folder for operations
     * @param input_folder - path to input data folder
     */
    public void set_input_folder(String input_folder)
    {
        if(check_folder_exists(input_folder) != null)
        {
            this.input_folder = input_folder;
        }
        else
        {
            Outputter.print_error("Folder doesn't exist");
        }

    }

    /**
     * Check if folder exists and return path to that folder if so
     * @param folder_path - path to folder that needs to be checked
     * @return path to folder if it exists, null object otherwise
     */
    private String check_folder_exists(String folder_path)
    {
        if (Files.exists(Paths.get(folder_path)))
        {
            return folder_path;
        }
        Outputter.print_error("Folder doesn't exist");
        return null;
    }

    private String input_folder;

    public Folder_Controller(String input_folder)
    {
        set_input_folder(input_folder);
    }

    /**
     * Remove all files inside passed directory
     *
     * @return 0 if clear succeeded
     */
    public int clear_directory()
    {
        try
        {
            FileUtils.cleanDirectory(new File(this.input_folder));
        }
        catch (IOException e)
        {
            Outputter.print_error(e.getMessage());
            return -1;
        }

        return 0;
    }

    /**
     * Get list of files in passed folder path
     *
     * @return ArrayList of files
     */
    private String[] get_files_list()
    {
        File folder = new File(this.input_folder);

        return folder.list();
    }

    // Parse chunk files in specified folder

    public void parse_chunks_folder_into_log_file(String output_file_full_path, Log_Parser parser, ArrayList<Pattern> patterns)
    {
        // Array of chunk arrays for keeping track of current file, first row reserved for header
        int chunks_found = 0;

        String[] files = this.get_files_list();

        // Parse files into relative chunks
        for(String filename: files)
        {
            ArrayList<ArrayList<String>> completed_array = new ArrayList<>();
            ArrayList<String> first_element = new ArrayList<>();
            first_element.add("Selects found/Records parsed: ");
            completed_array.add(first_element);

            // Get all chunks inside file
            ArrayList<ArrayList<String>> chunks = parser.get_chunks_from_file(
                    this.input_folder + "\\" + filename);
            // For each chunk call chunk parser
            for(ArrayList<String> chunk:chunks)
            {
                ArrayList<String> parsed_chunk = parser.parse_text_chunk(chunk, Regex_Strings.time_pattern, patterns);
                // Append parsed chunk to current file list of parsed lines
                completed_array.add(parsed_chunk);
            }

            chunks_found += chunks.size();

            // Append amount of chunks found and amount of records parsed to parsed dara list
            completed_array.get(0).add(Integer.toString(chunks_found));
            completed_array.get(0).add(Integer.toString(completed_array.size()-1));

            // Write parsed data from current file to output master file
            new File_Functions().write_file_data_to_master_file(
                    output_file_full_path,
                    filename,
                    completed_array,
                    false);

            // Reset chunks count
            chunks_found = 0;
        }

        Outputter.print_info("Total chunks found: "+chunks_found);
    }

    /**
     * Execute parse command on each file in passed folder, create chunk files in output folder
     * @param output_folder_path - folder to crate chunk files in
     * @param start_pattern - pattern to find beginning of the chunk
     * @param end_pattern - pattern to find end of the chunk
     * @return returns amount of chunks found and written to chunk file
     */
    public int parse_folder_into_chunks(String output_folder_path, Log_Parser parser,
                                                      Pattern start_pattern, Pattern end_pattern)
    {
        int start_patterns_found = 0;

        // Parse files into chunks with relative data
        for(String file_name:this.get_files_list())
        {

            Outputter.print_info(" ===== Now reading: " + file_name + " ===== ");

            String file_to_read_full_path = this.input_folder + "\\" + file_name;

            String output_file_full_path = output_folder_path + "\\" + file_name;
            start_patterns_found += parser.split_file_into_chunks(  file_to_read_full_path, output_file_full_path,
                                                                    start_pattern, end_pattern);
        }

        return start_patterns_found;
    }


    /**
     * Parse folder with log files into single master file containing log file name, start and end dates
     * @param output_file_full_path - path to file
     * @param parser - parser object to use for each file in folder
     */
    public void parse_folder_log_time(String output_file_full_path, Log_Parser parser)
    {
        // Parse files into relative chunks
        for (String file : Objects.requireNonNull(this.get_files_list()))
        {
            String log_full_path = this.input_folder + "\\" + file;
            ArrayList<String> run_time = parser.get_log_run_time(log_full_path, Regex_Strings.time_pattern);
            String string_to_write = file + delimiter + String.join(delimiter, run_time);
            new File_Functions().write_line_to_master_file(output_file_full_path, string_to_write);
        }

    }
}
