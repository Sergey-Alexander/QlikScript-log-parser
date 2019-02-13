package LogParserModule;

import java.util.regex.Pattern;

public class Regex_Strings
{
    // TODO refactor in dictionary form, non static implementation
    // RegEx patterns
    public static Pattern time_pattern = Pattern.compile("(?<target>(\\d{4}-\\d{2}-\\d{2})\\s(\\d{2}:\\d{2}:\\d{2}))");
    public static Pattern select_pattern = Pattern.compile(".*select([\\s\\n]|\\z)", Pattern.CASE_INSENSITIVE);
    public static Pattern from_pattern = Pattern.compile("from\\s+(?<target>[\\[\\]\\p{L}\\w\"/\\Q.\\E]*)", Pattern.CASE_INSENSITIVE);
    public static Pattern fields_pattern = Pattern.compile("\\s(?<target>\\d[\\d\\s]*)[ \\t]fields\\sfound", Pattern.CASE_INSENSITIVE);
    // Target located between timestamp and "lines fetched" substring
    public static Pattern lines_pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}(?<target>.*)line.*fetch", Pattern.CASE_INSENSITIVE);
    public static Pattern error_pattern = Pattern.compile(".*Error:", Pattern.CASE_INSENSITIVE);

    public static Pattern from_qvd_pattern_broad = Pattern.compile("from.*\\.qvd");

    public static Pattern store_pattern = Pattern.compile(".*STORE.*INTO((.*\\[)?(?<target>.*\\.qvd))", Pattern.CASE_INSENSITIVE);
    public static Pattern from_qvd_pattern = Pattern.compile("from((.*\\[)?(?<target>.*\\.qvd))", Pattern.CASE_INSENSITIVE);

}
