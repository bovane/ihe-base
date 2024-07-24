package org.openehealth.ipf.tutorials.xds.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.internal.MongoClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.xds.core.SampleData;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssigningAuthority;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/24
 */
@Slf4j
public class MongoTest {

    public static void main(String[] args) {
        // 创建MongoDB客户端
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        // 创建数据库
        MongoDatabase database = mongoClient.getDatabase("ihe-base");
        database.createCollection("entries");

        // 获取所有数据库名称
        MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();

        System.out.println("数据库名称:");
        for (String dbName : databaseNames) {
            System.out.println(dbName);
        }

        // 关闭MongoDB客户端
        mongoClient.close();
    }
}
