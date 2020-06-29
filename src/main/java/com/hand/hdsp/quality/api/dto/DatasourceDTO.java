package com.hand.hdsp.quality.api.dto;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 数据源
 *
 * @author 奔波儿灞 2019-05-30 10:40:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel("数据源")
public class DatasourceDTO extends AuditDomain {

    public static final String FIELD_DATASOURCE_ID = "datasourceId";
    public static final String FIELD_DATASOURCE_NAME = "datasourceName";
    public static final String FIELD_DATASOURCE_DESCRIPTION = "datasourceDescription";
    public static final String FIELD_DATASOURCE_TYPE = "datasourceType";
    public static final String FIELD_SETTINGS_INFO = "settingsInfo";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    private static final String CUSTOMIZE = "CUSTOMIZE";
    private static final String FTP = "FTP";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long datasourceId;

    @ApiModelProperty(value = "数据源名称")
    private String datasourceName;

    @ApiModelProperty(value = "数据源描述")
    private String datasourceDescription;

    @ApiModelProperty(value = "数据源分类")
    private String datasourceClass;

    @ApiModelProperty(value = "数据源类型(快码：HDSP.DATASOURCE_TYPE)")
    private String datasourceType;

    @ApiModelProperty(value = "驱动类型(快码：HDSP.DRIVER_TYPE)")
    private String driverType;

    @ApiModelProperty(value = "驱动ID")
    private Long driverId;

    @ApiModelProperty(value = "数据源配置")
    private String settingsInfo;

    @ApiModelProperty(value = "禁用启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "组织ID")
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    private String password;

    private String schema;

    private String sql;

    private Integer page;

    private Integer size;

    private String tableName;
}
