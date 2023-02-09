package com.hand.hdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import java.util.List;

/**
 * @Title: DataStandardExportDTO
 * @Description:
 * @author: lgl
 * @date: 2023/2/7 16:19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "数据标准", en = "DataStandard", rowOffset = 2)
public class DataStandardExportDTO extends GroupExportDTO {


    @ExcelColumn(zh = "数据标准列表", en = "dataStandardDTOList", child = true)
    private List<DataStandardDTO> dataStandardDTOList;
}
