//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.hzero.boot.alert.vo;

import org.hzero.boot.message.entity.Attachment;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class InboundMessage {
    private String alertCode;
    private Long tenantId;
    private String tenantNum;
    private String dataSource;
    private String alertInstCode;
    private Date startsAt;
    private Date endsAt;
    private List<Map<String, Object>> dataSet;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String generatorURL;
    private Integer asyncFlag;
    private Map<String, String> receiverArgs;

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    private List<Attachment> attachmentList;

    public InboundMessage() {
    }

    public String getAlertCode() {
        return this.alertCode;
    }

    public void setAlertCode(String alertCode) {
        this.alertCode = alertCode;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantNum() {
        return this.tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAlertInstCode() {
        return this.alertInstCode;
    }

    public void setAlertInstCode(String alertInstCode) {
        this.alertInstCode = alertInstCode;
    }

    public Date getStartsAt() {
        return this.startsAt;
    }

    public void setStartsAt(Date startsAt) {
        this.startsAt = startsAt;
    }

    public Date getEndsAt() {
        return this.endsAt;
    }

    public void setEndsAt(Date endsAt) {
        this.endsAt = endsAt;
    }

    public List<Map<String, Object>> getDataSet() {
        return this.dataSet;
    }

    public void setDataSet(List<Map<String, Object>> dataSet) {
        this.dataSet = dataSet;
    }

    public Map<String, String> getLabels() {
        return this.labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public Map<String, String> getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    public String getGeneratorURL() {
        return this.generatorURL;
    }

    public void setGeneratorURL(String generatorURL) {
        this.generatorURL = generatorURL;
    }

    public Integer getAsyncFlag() {
        return this.asyncFlag;
    }

    public void setAsyncFlag(Integer asyncFlag) {
        this.asyncFlag = asyncFlag;
    }

    public Map<String, String> getReceiverArgs() {
        return this.receiverArgs;
    }

    public void setReceiverArgs(Map<String, String> receiverArgs) {
        this.receiverArgs = receiverArgs;
    }
}
