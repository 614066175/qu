package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>规则告警等级表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleWarningLevelRepository extends BaseRepository<RuleWarningLevel, RuleWarningLevelDTO>, ProxySelf<RuleWarningLevelRepository> {

    /**
     * 删除
     *
     * @param ruleLineId
     * @return
     */
    int deleteByParentId(Long ruleLineId);

    /**
     * 列表
     *
     * @param ruleLineId
     * @param tenantId
     * @return
     */
    List<RuleWarningLevelDTO> list(Long ruleLineId, Long tenantId);

    /**
     * 列表（分页）
     *
     * @param pageRequest
     * @param ruleWarningLevelDTO
     * @return
     */
    Page<RuleWarningLevelDTO> list2(PageRequest pageRequest, RuleWarningLevelDTO ruleWarningLevelDTO);

    /**
     * 判断告警范围是否重叠
     *
     * @param ruleWarningLevelDTO ruleWarningLevelDTO
     * @return 为 null 则重叠
     */
    Integer judgeOverlap(RuleWarningLevelDTO ruleWarningLevelDTO);
}
