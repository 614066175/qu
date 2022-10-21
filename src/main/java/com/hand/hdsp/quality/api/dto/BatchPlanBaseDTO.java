package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>批数据方案-基础配置表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-基础配置表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "质检项", en = "batch plan base")
public class BatchPlanBaseDTO extends AuditDomain {


    //==================导出字段====================
    //头行，一个头对应多个行时，showInChildren = true只会在第一个child中显示。

    @ApiModelProperty("质检项编码")
    @ExcelColumn(zh = "质检项编码", en = "planBaseCode")
    private String planBaseCode;

    @ApiModelProperty("质检项名称")
    @ExcelColumn(zh = "质检项名称", en = "planBaseName")
    private String planBaseName;

    @ApiModelProperty(value = "数据源类型(快码：HDSP.DATASOURCE_TYPE)")
    @ExcelColumn(zh = "数据源类型", en = "datasourceType")
    private String datasourceType;

    @ApiModelProperty(value = "数据源编码")
    @ExcelColumn(zh = "数据源编码", en = "datasourceCode")
    private String datasourceCode;

    @ApiModelProperty(value = "数据库")
    @ExcelColumn(zh = "数据库", en = "datasourceSchema")
    private String datasourceSchema;

    @ApiModelProperty(value = "类型 HDSP.XQUA.SQL_TYPE (TABLE/VIEW/SQL)")
//    @ExcelColumn(zh = "类型", en = "sqlType")
    @LovValue(lovCode = "HDSP.XQUA.SQL_TYPE",meaningField = "sqlTypeMeaning")
    private String sqlType;

    @ExcelColumn(zh = "类型", en = "sqlType")
    private String sqlTypeMeaning;

    @ApiModelProperty(value = "表名/视图名/自定义SQL")
    @ExcelColumn(zh = "表名称/视图名称/查询语句", en = "objectName")
    private String objectName;

    @ApiModelProperty(value = "描述")
    @ExcelColumn(zh = "描述", en = "description")
    private String description;

    @ApiModelProperty(value = "增量校验策略")
//    @ExcelColumn(zh = "增量同步策略", en = "incrementStrategy")
    @LovValue(lovCode = "HDSP.XFAC.INCREMENT_STRATEGY",meaningField = "incrementStrategyMeaning")
    private String incrementStrategy;

    @ExcelColumn(zh = "增量同步策略", en = "incrementStrategy")
    private String incrementStrategyMeaning;

    @ApiModelProperty(value = "增量字段")
    @ExcelColumn(zh = "增量字段", en = "incrementColumn")
    private String incrementColumn;

    @ApiModelProperty(value = "条件where")
    @ExcelColumn(zh = "数据过滤", en = "whereCondition")
    private String whereCondition;

    @ExcelColumn(zh = "表级规则", en = "batchPlanTableDTOList", child = true)
    private List<BatchPlanTableDTO> batchPlanTableDTOList;

    @ExcelColumn(zh = "字段规则", en = "batchPlanFieldDTOList", child = true)
    private List<BatchPlanFieldDTO> batchPlanFieldDTOList;

    @ExcelColumn(zh = "表间规则", en = "batchPlanRelTableDTOList", child = true)
    private List<BatchPlanRelTableDTO> batchPlanRelTableDTOList;

    @ExcelColumn(zh = "质检项表单值", en = "baseFormValueDTOList", child = true)
    private List<BaseFormValueDTO> baseFormValueDTOList;


    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planBaseId;


    @ApiModelProperty(value = "批数据评估方案表XQUA_BATCH_PLAN.PLAN_ID")
    @NotNull
    private Long planId;


    @ApiModelProperty(value = "数据源ID")
    private Long datasourceId;


    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long tableNum;

    private Long fieldNum;

    private Long relTableNum;

    private String datasourceName;

    @Transient
    private String planName;

    @Transient
    private String planCode;

    @Transient
    private Long groupId;

    @Transient
    private List<Long> planIds;

    @Transient
    private List<Long> planBaseIds;

    @Transient
    private List<BaseFormValueDTO> baseFormValueDTOS;

    @Transient
    private Long currentPlanId;

    //质检项所处的当前方案
    @Transient
    private String currentPlanName;

    @Transient
    private String baseFormValueJson;

    @Transient
    private Integer editFlag;

    private Long projectId;

    @ApiModelProperty(value = "是否基于标准生成规则，0 否 | 1 是")
    private Integer buildRuleFlag;

    @Transient
    private String groupName;

    @Transient
    private Long parentGroupId;

    @Transient
    private String groupPath;

    @Transient
    private String projectName;
}
