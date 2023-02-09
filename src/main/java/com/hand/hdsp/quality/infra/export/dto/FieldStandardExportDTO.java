package com.hand.hdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
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
@ExcelSheet(zh = "字段标准", en = "Field Standard", rowOffset = 2)
public class FieldStandardExportDTO extends GroupExportDTO {


    @ExcelColumn(zh = "字段标准列表", en = "dataFieldDTOList", child = true)
    private List<DataFieldDTO> dataFieldDTOList;
}
