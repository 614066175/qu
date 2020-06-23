package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * description
 *
 * @author LZL 2018/11/06 13:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ColumnDTO extends AuditDomain {
    /**
     * 索引
     */
    private String colIndex;
    /**
     * 列名
     */
    private String colName;
    /**
     * 类型
     * @see java.sql.Types
     */
    private Integer dataType;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 长度
     */
    private Integer colSize;
    /**
     * 精度
     */
    private Integer accuracy;
    /**
     * 默认值
     */
    private String colDef;
    /**
     * 允许为空
     */
    private String nullAble;
    /**
     * 备注
     */
    private String remarks;
    /**
     *
     */
    private String isGeneratedcolumn;
    /**
     * 是否自增
     */
    private String isAutoincrement;

    private String sql;
}
