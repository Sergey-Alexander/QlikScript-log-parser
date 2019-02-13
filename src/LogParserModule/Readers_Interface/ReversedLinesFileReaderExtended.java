package LogParserModule.Readers_Interface;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public final class ReversedLinesFileReaderExtended extends ReversedLinesFileReader implements LineReaderInterface {
    private static final int DEFAULT_BLOCK_SIZE = 4096;

    public ReversedLinesFileReaderExtended(final File file, final Charset charset) throws IOException
    {
        super(file, DEFAULT_BLOCK_SIZE, charset);
    }

    @Override
    public String readLine() throws IOException
    {
        return super.readLine();
    }
}
