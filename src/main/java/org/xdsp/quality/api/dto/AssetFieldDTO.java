package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.xdsp.core.base.mybatis.annotation.Fuzzy;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>字段资产表 数据传输对象</p>
 *
 * @author zhong.wu@hand-china.com 2020-05-11 19:56:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段资产表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class AssetFieldDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long fieldId;

    @ApiModelProperty(value = "资产ID XASS_ASSET.ASSET_ID")
    @NotNull
    private Long assetId;

    @ApiModelProperty(value = "表资产ID XASS_ASSET_TABLE.TABLE_ASSET_ID")
    @NotNull
    private Long tableAssetId;

    @ApiModelProperty(value = "字段名")
    @NotBlank
    @Size(max = 250)
    @Fuzzy
    private String fieldName;

    @ApiModelProperty(value = "字段描述")
    @Fuzzy
    private String fieldDesc;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "长度")
    private Long length;

    @ApiModelProperty(value = "小数位")
    private Long decimals;

    @ApiModelProperty(value = "是否可为空 1-是 0-否")
    private Integer nullFlag;

    @ApiModelProperty(value = "默认值")
    @Fuzzy
    private String defaultValue;

    @ApiModelProperty(value = "是否为主键 1-是 0-否")
    private Integer keyFlag;

    @ApiModelProperty(value = "是否为外键 1-是 0-否")
    private Integer foreignKeyFlag;

    @ApiModelProperty(value = "空值")
    private BigDecimal nullValueRate;

    @ApiModelProperty(value = "重复")
    private BigDecimal repetitionRate;

    @ApiModelProperty(value = "非重复")
    private BigDecimal nonRepetitionRate;

    @ApiModelProperty(value = "是否有效 1-是 0-否")
    private Integer validFlag;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "发布时间")
    private Date releaseDate;

    @ApiModelProperty(value = "撤销发布时间")
    private Date cancelReleaseDate;

    @ApiModelProperty(value = "发布人ID")
    private Long releaseUserId;

    @ApiModelProperty(value = "备注")
    @Fuzzy
    private String remark;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "元模型ID")
    private Long modelHeaderId;

    @ApiModelProperty(value = "安全等级")
    private String safeMode;

    @Transient
    @Fuzzy
    private String tenantName;

    @Transient
    private Date collectionDate;

    @Transient
    private String releaseStatus;

    @Transient
    private Integer selected;

    @Transient
    private Long assetFieldId;

    @Transient
    private String assetName;

    @Transient
    private String assetType;

    @Transient
    private String assetCode;

    @Transient
    private String assetDesc;

    @Transient
    private String nameLevelPath;

    @Transient
    private String metadataType;

    @Transient
    private String catalog;

    @Transient
    private String catalogName;

    @Transient
    private String businessName;

    @Transient
    private String basicsName;

    @Transient
    private Date basicsLastUpdateDate;

    @Transient
    private String collectionType;

    @Transient
    private Long datasourceId;

    @Transient
    private String datasourceSchema;

    @Transient
    private String tableName;

    @Transient
    private String datasourceType;

    @Transient
    private String icon;

    @Transient
    private String datasourceCode;

    @Transient
    private String schemaId;

    @Transient
    private String indexName;

    @Transient
    private Long indexId;

    @Transient
    private Long unrelatedIndexId;

    @Transient
    private String relateType;

    @Transient
    private Long standardId;

    @Transient
    private Long unAimStandardId;

    @Transient
    private String aimType;

    @Transient
    private List<Long> standardIdList;

    /**
     * 数据源租户id
     */
    @Transient
    private Long datasourceTenantId;
}

