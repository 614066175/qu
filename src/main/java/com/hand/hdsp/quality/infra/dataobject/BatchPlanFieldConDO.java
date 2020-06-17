package com.hand.hdsp.quality.infra.dataobject;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.List;

/**
 * <p>批数据方案-字段规则条件表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanFieldConDO extends AuditDomain {

    private Long conditionId;

    private Long planLineId;

    private String whereCondition;

    private String compareWay;

    private String warningLevel;

    private List<WarningLevelDTO> warningLevelList;

    private Long tenantId;

    private Long planRuleId;

    private String checkWay;

    private String checkItem;

    private String countType;

    private String fieldName;

    private String checkFieldName;

}
