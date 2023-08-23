package org.xdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.xdsp.quality.api.dto.RuleDTO;

import java.util.List;

/**
 * @Title: StandardRuleExportDTO
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 15:23
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "标准规则", en = "Standard Rule", rowOffset = 2)
public class StandardRuleExportDTO extends GroupExportDTO {

    @ExcelColumn(zh = "标准规则列表", en = "ruleDTOList", child = true)
    private List<RuleDTO> ruleDTOList;
}
