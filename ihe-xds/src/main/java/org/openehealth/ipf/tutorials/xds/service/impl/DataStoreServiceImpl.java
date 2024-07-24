package org.openehealth.ipf.tutorials.xds.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Association;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.SubmissionSet;
import org.openehealth.ipf.tutorials.constant.IheConstant;
import org.openehealth.ipf.tutorials.xds.DataStore;
import org.openehealth.ipf.tutorials.xds.entity.BinaryData;
import org.openehealth.ipf.tutorials.xds.service.DataStoreService;
import org.openehealth.ipf.tutorials.xds.util.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/24
 */
@Service
@Slf4j
public class DataStoreServiceImpl implements DataStoreService {

    @Autowired
    private DataStore dataStore;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void listDataStore()  {
        var entries = (CopyOnWriteArrayList)dataStore.getEntries();
        var documents = (ConcurrentHashMap)dataStore.getDocuments();
        var indexs =  (LinkedHashMap<String, ConcurrentHashMap>)dataStore.getIndexes();
        log.error("遍历注册库里面的内容并存储到对象数据库: ");
        entries.forEach(entry ->{
            log.info(entry.toString());
            BinaryData binaryData = null;
            try {
                binaryData = entryToBinaryData(entry);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            save(binaryData, IheConstant.ENTRIES);
        });

        log.warn("查看注册库的所有内容: ");
        listAll(IheConstant.ENTRIES);

        log.error("遍历存储库里面的内容: ");
        documents.forEach((key, value) -> {
            BinaryData binaryData = new BinaryData();
            binaryData.setId(key.toString());
            binaryData.setData((byte[]) value);
            System.out.println("Key: " + binaryData.getId() + ", Value: " + IOUtils.toString(binaryData.getData()));
            save(binaryData,IheConstant.DOCUMENTS);
        });
        log.error("遍历Indexes里面的内容:");
        for (ConcurrentHashMap innerMap : indexs.values()) {
            for (Object innerKey : innerMap.keySet()) {
                System.out.println("Inner Key: " + innerKey + ", Inner Value: " + innerMap.get(innerKey));
            }
        }

    }

    public BinaryData entryToBinaryData(Object object) throws Exception {
        BinaryData binaryData = new BinaryData();
        if (object instanceof DocumentEntry) {
            DocumentEntry documentEntry = (DocumentEntry) object;
            binaryData.setId(documentEntry.getEntryUuid());
            binaryData.setData(SerializationUtil.serialize(documentEntry));
        }
        else if (object instanceof SubmissionSet) {
            SubmissionSet submissionSet = (SubmissionSet) object;
            binaryData.setId(submissionSet.getEntryUuid());
            binaryData.setData(SerializationUtil.serialize(submissionSet));

        }
        else if (object instanceof Association) {
            Association association = (Association) object;
            binaryData.setId(association.getEntryUuid());
            binaryData.setData(SerializationUtil.serialize(association));

        }
        return binaryData;
    }
    public String save(BinaryData serializedData, String collectionName) {
        // 创建查询条件，假设您要根据 data 字段检查是否存在
        Query query = new Query();
        query.addCriteria(Criteria.where("data").is(serializedData.getData()));
        if (!mongoTemplate.exists(query, BinaryData.class, collectionName)) {
            BinaryData savedData = mongoTemplate.insert(serializedData, collectionName);
            System.out.println("Inserted document ID: " + savedData.getId()); // 打印 ID 信息
            return savedData.getId();
        }
        return null;
    }

    public void listAll(String collectionName) {
        // 查找
        List<BinaryData> allDocuments = mongoTemplate.findAll(BinaryData.class, collectionName);
        Object object;
        for (BinaryData binaryData : allDocuments) {
            log.info(binaryData.getId());
            try {
                object = SerializationUtil.deserialize(binaryData.getData());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (object instanceof DocumentEntry) {
                DocumentEntry documentEntry = (DocumentEntry) object;
                log.info(documentEntry.toString());
            }
            else if (object instanceof SubmissionSet) {
                SubmissionSet submissionSet = (SubmissionSet) object;
                log.info(submissionSet.toString());
            }
            else if (object instanceof Association) {
                Association association = (Association) object;
                log.warn(association.toString());
            }
        }
    }
}
