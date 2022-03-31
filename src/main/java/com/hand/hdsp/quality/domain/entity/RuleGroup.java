package com.hand.hdsp.quality.domain.entity;

import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hzero.mybatis.annotation.Unique;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 规则分组表实体
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 11:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_rule_group")
public class RuleGroup extends AuditDomain {

    public static final RuleGroup ROOT_RULE_GROUP = RuleGroup.builder()
            .groupId(0L)
            .groupCode("root")
            .groupName("所有分组")
            .build();

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验编码是否存在
     *
     * @param ruleGroupRepository 资源库
     */
    public void validCodeRepeat(RuleGroupRepository ruleGroupRepository) {
        RuleGroup ruleGroup = new RuleGroup();
        ruleGroup.setGroupCode(groupCode);
        int num = ruleGroupRepository.selectCount(ruleGroup);

        if (num > 0) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
    }

    /**
     * 校验父级分组ID
     * 父级分组ID不能为空
     */
    public void validParentIdAndTemplate() {
        if (this.getParentGroupId() == null) {
            throw new CommonException(ErrorCode.PARENT_NULL);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long groupId;

    private Long parentGroupId;

    @Unique
    private String groupCode;

    private String groupName;

    private String groupDesc;

    private Long tenantId;

    private Long projectId;
}
