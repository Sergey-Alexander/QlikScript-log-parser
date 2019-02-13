# QlikScript-log-parser

Parser for qvw.log files.
Current functionality:
* Splits files into files containing chunks starting from SELECT statment to "lines found" statement. 
* Afterwards parses said chunks for table/tables select grabs fields from, amount of fields in said tables and amount of rows fetched.
* Collects total runtime of each log file and combines them into master log file

### Running

Running main function will prompt user for logs folder, output folder, master log file for parsing and master log file for runtimes.
