package org.openehealth.ipf.tutorials.xds.datasource;

import cn.hutool.core.io.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 通过加载本地文件作为文档内容.将本地文件作为流读取
 * @author bovane
 */
@Data
@AllArgsConstructor
public class CustomDataSource implements DataSource {
    /** Content stream size for tests with large content. Must be larger than 64K to ensure
     * CXF does use MTOM. */
    private String filePath;
    private String contentType;
    private String name;

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream inputStream = FileUtil.getInputStream(this.filePath);
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
