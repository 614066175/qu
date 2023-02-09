package com.hand.hdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
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
@ExcelSheet(zh = "命名标准", en = "Name Standard", rowOffset = 2)
public class NameStandardExportDTO extends GroupExportDTO {


    @ExcelColumn(zh = "命名标准列表", en = "nameStandardDTOList", child = true)
    private List<NameStandardDTO> nameStandardDTOList;
}
