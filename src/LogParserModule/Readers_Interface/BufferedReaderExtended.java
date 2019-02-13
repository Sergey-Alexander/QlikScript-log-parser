package LogParserModule.Readers_Interface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class BufferedReaderExtended extends BufferedReader implements LineReaderInterface {
    public BufferedReaderExtended(final Reader reader) {
        super(reader);
    }

    @Override
    public String readLine() throws IOException
    {
        return super.readLine();
    }
}
