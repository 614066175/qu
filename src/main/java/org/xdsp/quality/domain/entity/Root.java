package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

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
    public static final String FIELD_RELEASE_BY = "releaseBy";
    public static final String FIELD_RELEASE_DATE = "releaseDate";

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
    @Transient
    @ApiModelProperty(value = "分组路径")
    @ExcelColumn(zh = "分组路径", en = "groupPath")
    private String groupPath;

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
     * 责任人邮箱
     */
    @ExcelColumn(zh = "责任人电话", en = "chargeEmail")
    @Transient
    private String chargeEmail;

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
    private Long instanceId;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date startUpdateDate;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date endUpdateDate;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date startCreateDate;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date endCreateDate;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date releaseDateFrom;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date releaseDateTo;

    /**
     * 分组集合
     */
    @Transient
    private Long[] groupArrays;

    /**
     * 勾选导出传入的ids
     */
    @Transient
    private String exportIds;

    @ApiModelProperty("发布人id")
    private Long releaseBy;

    @Transient
    private String releaseByName;

    @ApiModelProperty("发布时间")
    private Date releaseDate;

    /**
     * 分组路径
     */
    @Transient
    private List<Long> exportIdList;
}