package org.xdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.xdsp.quality.api.dto.StandardDocDTO;

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
@ExcelSheet(zh = "标准文档", en = "Doc Standard", rowOffset = 2)
public class DocStandardExportDTO extends GroupExportDTO {


    @ExcelColumn(zh = "标准文档列表", en = "standardDocDTOList", child = true)
    private List<StandardDocDTO> standardDocDTOList;
}
