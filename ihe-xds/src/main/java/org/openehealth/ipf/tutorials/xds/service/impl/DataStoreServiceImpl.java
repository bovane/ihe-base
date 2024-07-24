package org.openehealth.ipf.tutorials.xds.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.tutorials.xds.DataStore;
import org.openehealth.ipf.tutorials.xds.service.DataStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    @Override
    public void listDataStore() {
        var entries = (CopyOnWriteArrayList)dataStore.getEntries();
        var documents = (ConcurrentHashMap)dataStore.getDocuments();
        var indexs =  (LinkedHashMap<String, ConcurrentHashMap>)dataStore.getIndexes();
        log.error("遍历注册库里面的内容: ");
        entries.forEach(entry ->{
            log.info(entry.toString());
        });
        log.error("遍历存储库里面的内容: ");
        documents.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + IOUtils.toString((byte[]) value));
        });
        log.error("遍历Indexs里面的内容:");
        for (ConcurrentHashMap innerMap : indexs.values()) {
            for (Object innerKey : innerMap.keySet()) {
                System.out.println("Inner Key: " + innerKey + ", Inner Value: " + innerMap.get(innerKey));
            }
        }

    }
}
