package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 血缘DTO
 * </p>
 *
 * @author lgl 2021/10/15 16:49
 * @since 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineageDTO {
    //此表生成的血缘pk
    private String pk;

    private String datasourceCode;

    private String schemaName;

    private String tableName;

    private Integer qualityFlag;

    private Integer jobErrorFlag;

    private Integer productionFlag;

    private Long tenantId;
}
