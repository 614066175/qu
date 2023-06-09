package org.xdsp.quality.infra.export.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.xdsp.quality.api.dto.ReferenceDataDTO;

import javax.persistence.Transient;
import java.util.List;

/**
 * 参考数据导出
 * @author fuqiang.luo@hand-china.com
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "分组", en = "Group", rowOffset = 2)
public class ReferenceDataExportDTO extends GroupExportDTO{

    @ExcelColumn(zh = "参考数据列表", en = "referenceDataDTOList", child = true)
    @Transient
    private List<ReferenceDataDTO> referenceDataDTOList;
}
