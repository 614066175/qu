package com.hand.hdsp.quality.infra.vo;

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

    private Long id;

    private String groupId;

    private String parentGroupId;

    private String groupCode;

    private String groupName;

    private String type;

    private Long tenantId;
}
