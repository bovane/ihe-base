package org.openehealth.ipf.tutorials.xds.datasource;

import org.junit.jupiter.api.Assertions;
import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Data source used in tests to provide a large content stream.
 * @author Jens Riemschneider
 */
public class XmlDataSource implements DataSource {
    /** Content stream size for tests with large content. Must be larger than 64K to ensure
     * CXF does use MTOM. */
    public static final int STREAM_SIZE = 70000;

    @Override
    public InputStream getInputStream() throws IOException {
        return new InputStream() {
            private int idx;

            @Override
            public int read() throws IOException {
                if (idx >= STREAM_SIZE) {
                    return -1;
                }
                ++idx;
                return 66;
            }

            @Override
            public void close() throws IOException {
                Assertions.assertEquals(STREAM_SIZE, idx);
                super.close();
            }
        };
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public String getName() {
        return "dummy";
    }
}
