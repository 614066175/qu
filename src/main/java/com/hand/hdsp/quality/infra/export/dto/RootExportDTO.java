package com.hand.hdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.domain.entity.Root;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import java.util.List;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/02/10 14:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "词根",en = "Root Group",rowOffset = 2)
public class RootExportDTO extends GroupExportDTO{
    @Transient
    @ExcelColumn(zh = "词根列表", en = "rootDTOS", child = true)
    private List<Root> roots;
}
