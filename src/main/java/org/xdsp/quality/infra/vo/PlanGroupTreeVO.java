package org.xdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/30 21:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlanGroupTreeVO {

    public static final PlanGroupTreeVO ROOT_PLAN_GROUP = PlanGroupTreeVO.builder()
            .id(0L)
            .groupCode("root")
            .groupName("所有分组")
            .type("group")
            .groupId("group0")
            .build();

    private Long id;

    private String groupId;

    private String parentGroupId;

    private Long parentId;

    private String groupCode;

    private String groupName;

    private String planName;

    private String groupType;

    private String type;

    private Long tenantId;

    //质检项Id，分配列表查询时传递
    private Long planBaseId;

    //是否被分配标识（1是，0否）
    private Integer assignedFlag;

    //编辑标识（1可编辑，0不可）
    private Integer editFlag;

    private Long projectId;
}
