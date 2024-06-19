package org.openehealth.ipf.tutorials.xds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* @author bovane bovane.ch@gmial.com
* @create 2024/6/17
*/
@TableName(value = "xds_audit")
public class XdsAudit {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**
     * 审计日志
     */
    @TableField(value = "audit_message")
    private String auditMessage;

    /**
     * 获取ID
     *
     * @return id - ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取审计日志
     *
     * @return audit_message - 审计日志
     */
    public String getAuditMessage() {
        return auditMessage;
    }

    /**
     * 设置审计日志
     *
     * @param auditMessage 审计日志
     */
    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }
}