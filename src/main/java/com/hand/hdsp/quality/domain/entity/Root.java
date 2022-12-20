package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;

import com.hand.hdsp.quality.api.dto.RootDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * <p>词根实体</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_root")
@ExcelSheet(zh = "词根", en = "Root",rowOffset = 2)
public class Root extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_ROOT_EN_SHORT = "rootEnShort";
    public static final String FIELD_ROOT_EN = "rootEn";
    public static final String FIELD_ROOT_DESC = "rootDesc";
    public static final String FIELD_CHARGE_DEPT_ID = "chargeDeptId";
    public static final String FIELD_CHARGE_ID = "chargeId";
    public static final String FIELD_RELEASE_STATUS = "releaseStatus";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long id;

    private Long groupId;

    @ExcelColumn(zh = "词根英文简称", en = "rootEnShort")
    private String rootEnShort;

    /**
     * 词根中文
     */
    @Transient
    @ExcelColumn(zh = "词根中文名", en = "rootName")
    private String rootName;

    @ExcelColumn(zh = "词根英文全称", en = "rootEn")
    private String rootEn;

    @ExcelColumn(zh = "词根描述", en = "rootDesc")
    private String rootDesc;

    private Long chargeDeptId;

    private Long chargeId;

    private String releaseStatus;

    private Long tenantId;

    private Long projectId;

    /**
     * 责任人部门
     */
    @ExcelColumn(zh = "责任部门", en = "chargeDept")
    @Transient
    private String chargeDept;

    /**
     * 责任人名
     */
    @ExcelColumn(zh = "责任人", en = "chargeName")
    @Transient
    private String chargeName;

    /**
     * 责任人电话
     */
    @ExcelColumn(zh = "责任人电话", en = "chargeTel")
    @Transient
    private String chargeTel;

    /**
     * 责任人电话
     */
    @ExcelColumn(zh = "责任人电话", en = "chargeEmail")
    @Transient
    private String chargeEmail;

    /**
     * 分组编码
     */
    @Transient
    private String groupCode;

    /**
     * 分组编码
     */
    @Transient
    private String groupName;

    /**
     * 最后更新人
     */
    @Transient
    private String lastUpdatedName;

    /**
     * 最后更新人id
     */
    private Long lastUpdatedBy;


    @Transient
    private String startUpdateDate;

    @Transient
    private String endUpdateDate;

    @Transient
    private String startCreateDate;

    @Transient
    private String endCreateDate;

}