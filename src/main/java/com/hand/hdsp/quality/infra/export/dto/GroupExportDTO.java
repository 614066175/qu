package com.hand.hdsp.quality.infra.export.dto;

import lombok.Data;
import org.hzero.export.annotation.ExcelColumn;

/**
 * @Title: CommonGroupDTO
 * @Description:
 * @author: lgl
 * @date: 2023/2/7 16:16
 */
@Data
public class GroupExportDTO {
    public Long groupId;
    public String groupType;

    @ExcelColumn(zh = "分组名称",en = "groupName",order = -2)
    public String groupName;
    public String groupPath;
    public Long parentGroupId;
    public Long tenantId;
    public Long projectId;

    @ExcelColumn(zh = "父分组全路径",en = "parentGroupPath",order = -1)
    private String parentGroupPath;

}
