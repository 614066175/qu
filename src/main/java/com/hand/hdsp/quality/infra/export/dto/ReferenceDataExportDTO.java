package com.hand.hdsp.quality.infra.export.dto;

import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

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
