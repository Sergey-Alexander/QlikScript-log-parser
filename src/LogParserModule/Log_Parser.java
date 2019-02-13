package LogParserModule;

import LogParserModule.Readers_Interface.BufferedReaderExtended;
import LogParserModule.Readers_Interface.LineReaderInterface;
import LogParserModule.Readers_Interface.ReversedLinesFileReaderExtended;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log_Parser
{
    public static void set_chunk_separator(String chunk_separator)
    {
        Log_Parser.chunk_separator = chunk_separator;
    }

    private static String chunk_separator = "=====";

    public static void set_target_group_name(String target_group_name)
    {
        Log_Parser.target_group_name = target_group_name;
    }

    private static String target_group_name = "target";

    // TODO parallel

    /**
     * Go over file and write down chunks starting with start pattern and ending with end pattern
     *
     * @param file_to_read_full_path - file to read through
     * @param output_full_path       - file to write to
     * @param start_pattern          - pattern to be found at the start of target chunk
     * @param end_pattern            - pattern indicating end of the chunk
     * @return number of chunks found
     */
    public int split_file_into_chunks(String file_to_read_full_path,
                                      String output_full_path,
                                      Pattern start_pattern,
                                      Pattern end_pattern)

    {
        File_Functions log_writer = new File_Functions();
        int start_substring_counter = 0;
        ArrayList<String> current_chunk = new ArrayList<>();
        // Read lines from file
        try (BufferedReader file_stream = new BufferedReader(new FileReader(file_to_read_full_path)))
        {
            boolean chunk_start_found = false;

            String line;
            // For each line in file
            while ((line = file_stream.readLine()) != null)
            {
                // Find substring and store it
                boolean start_pattern_found = start_pattern.matcher(line).find();
                // If substring found
                if (start_pattern_found)
                {
                    start_substring_counter++;

                    Outputter.print_info(start_substring_counter + " Start pattern found!");

                    chunk_start_found = true;
                }

                if (chunk_start_found)
                {
                    // Write down lines to file until
                    current_chunk.add(line);

                    // Abort loop if sql request ended with error
                    // TODO decrease start_substring_counter by 1?
                    if (Regex_Strings.error_pattern.matcher(line).find())
                    {
                        chunk_start_found = false;
                        // Reset chunk data
                        current_chunk.clear();
                        continue;
                    }

                    boolean end_pattern_found = end_pattern.matcher(line).find();

                    if (end_pattern_found)
                    {
                        Outputter.print_info(start_substring_counter + " chunk ended");
                        // == Finishing up
                        chunk_start_found = false;
                        // Add chunk separator
                        current_chunk.add(chunk_separator);
                        // Write chunk
                        log_writer.write_to_file(output_full_path, current_chunk);
                        current_chunk.clear();
                    }
                }
            }
        }
        catch (Exception e)
        {
            Outputter.print_error(e.getMessage());
            return -1;
        }


        Outputter.print_info("Chunk parsing finished, writing data");

        current_chunk.add("Chunks found " + start_substring_counter);
        log_writer.write_to_file(output_full_path, current_chunk);

        return start_substring_counter;
    }

    public ArrayList<ArrayList<String>> get_chunks_from_file(String file_to_read_full_path)
    {
        ArrayList<ArrayList<String>> retrieved_chunks = new ArrayList<>();

        ArrayList<String> tmpChunk = new ArrayList<>();

        // TODO re implement as readline in case a lot of chunks are found
        try
        {
            List<String> lines = Files.readAllLines(Paths.get(file_to_read_full_path));

            for (String line : lines)
            {
                if (line.equals(chunk_separator))
                {
                    retrieved_chunks.add(new ArrayList<>(tmpChunk));
                    tmpChunk.clear();
                } else
                {
                    tmpChunk.add(line);
                }
            }
            return retrieved_chunks;
        }
        catch (IOException e)
        {
            Outputter.print_error(e.getMessage());
            return null;
        }

    }

    /**
     * Returns first regex result found in passed ArrayList according to time_pattern regex pattern
     * @param block        - ArrayList of string to look over
     * @param pattern - regex pattern to use while searching
     * @return found capture group if success, "-1" of regex search was unsuccessful
     */
    public String get_first_pattern_occurrence(ArrayList<String> block, Pattern pattern)
    {
        String pattern_result = "-1";
        for (String line : block)
        {
            Matcher start_match = pattern.matcher(line);

            if (start_match.find())
            {
                pattern_result = start_match.group(target_group_name);
                break;
            }
        }
        return pattern_result;
    }

    public ArrayList<String> get_chunk_run_time(ArrayList<String> chunk, Pattern time_pattern)
    {
        ArrayList<String> chunk_time = new ArrayList<>();
        String start_time = get_first_pattern_occurrence(chunk, time_pattern);

        ArrayList<String> reversed_chunk = chunk;
        Collections.reverse(reversed_chunk);
        String end_time = get_first_pattern_occurrence(reversed_chunk, time_pattern);

        chunk_time.add(start_time);
        chunk_time.add(end_time);

        return chunk_time;
    }


    /**
     * Returns ArrayList with start end end time of passed log file
     * @param file_to_read_full_path - full path to log file
     * @param pattern - pattern used to find at the beginning and at the end of the file
     * @return ArrayList with two found patterns or filled with "-1"
     */
    public ArrayList<String> get_log_run_time(String file_to_read_full_path, Pattern pattern)
    {
        ArrayList<String> file_time = new ArrayList<>();
        String start_time = "-1";
        String end_time = "-1";

        try (BufferedReaderExtended file_reader = new BufferedReaderExtended(new FileReader(file_to_read_full_path)))
        {
            start_time = get_first_occurrence_line_by_line(file_reader, pattern);
        }
        catch (Exception e)
        {
            Outputter.print_error(e.getMessage());
            return null;
        }


        // Read file backwards and catch first time stamp occurrence
        try (ReversedLinesFileReaderExtended file_reader = new ReversedLinesFileReaderExtended(
                                                new File(file_to_read_full_path),
                                                Charset.forName("UTF-8")))
        {
            end_time = get_first_occurrence_line_by_line(file_reader, pattern);
        }
        catch (Exception e)
        {
            Outputter.print_error(e.getMessage());
            return null;
        }

        file_time.add(start_time);
        file_time.add(end_time);

        return file_time;
    }

    /**
     * Finds first occurence of pattern in stream reading line by line
     * @param file_stream - stream to read lines from
     * @param pattern - pattern to find in each line
     * @return matched string
     * @throws IOException - throws IOException as inherited from readers
     */
    private String get_first_occurrence_line_by_line(LineReaderInterface file_stream, Pattern pattern) throws IOException
    {
        // TODO limit iteration count in case passed file has no patterns
        String line;

        while ((line = file_stream.readLine()) != null)
        {
            Matcher start_match = pattern.matcher(line);

            if (start_match.find())
            {
                return start_match.group(target_group_name);
            }
        }

        return "-1";
    }

    ArrayList<String> parse_text_chunk(ArrayList<String> chunk,
                                       Pattern time_pattern,
                                       ArrayList<Pattern> patterns_to_find_in_chunk) {
        // Array containing records for current chunk
        // Initializing space for each pattern
        ArrayList<String>  current_chunk_parsed_data = new ArrayList<>();
        for(int i = 0; i < patterns_to_find_in_chunk.size(); i++)
        {
            current_chunk_parsed_data.add(null);
        }

        for(String line:chunk)
        {
            for(int i = 0; i < patterns_to_find_in_chunk.size(); i++)
            {
                Matcher pattern_match = patterns_to_find_in_chunk.get(i).matcher(line);

                // From can be on same line as select
                if(pattern_match.find()) {
                    String found_group = pattern_match.group(target_group_name);
                    found_group = found_group.trim();

                    // Append table name to chunk
                    // If another from is found, append to same field as it probably means join or union going on
                    // Or subquery
                    current_chunk_parsed_data.set(i,
                            current_chunk_parsed_data.get(i) == null ?
                            found_group :
                            current_chunk_parsed_data.get(i) + "/" + found_group);
                    Outputter.print_info(found_group);
                }
            }
        }

        // Reached end of the chunk
        // Find end time from regex
        ArrayList<String> chunk_run_time = get_chunk_run_time(chunk, time_pattern);

        // === Storing ===
        // Append run time
        current_chunk_parsed_data.addAll(chunk_run_time);

        // Add new line element for printing and writing alike
        // current_chunk_parsed_data.append("\n")

        return current_chunk_parsed_data;
    }
}
