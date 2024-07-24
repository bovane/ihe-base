package org.openehealth.ipf.tutorials.xds.entity;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/24
 */
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "myCollection")
@Data
public class BinaryData {

    @Id
    private String id;
    private byte[] data;

    public BinaryData(){

    }
    public BinaryData(byte[] data) {
        this.data = data;
    }

//    public byte[] getData() {
//        return data;
//    }
//
//    public void setData(byte[] data) {
//        this.data = data;
//    }
}

