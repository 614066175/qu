package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleMapper extends BaseMapper<Rule> {

    /**
     * 列表
     *
     * @param ruleCodeList
     * @param ruleModel
     * @param tenantId
     * @return
     */
    List<RuleDTO> list(@Param("ruleCode") List<String> ruleCodeList, @Param("ruleModel") String ruleModel, @Param("tenantId") Long tenantId);

    /**
     * 查询所有
     *
     * @param ruleDTO
     * @return
     */
    List<RuleDTO> listAll(RuleDTO ruleDTO);

    /**
     * 列表（租户级）
     *
     * @param ruleDTO 查询条件
     * @return
     */
    List<RuleDTO> listTenant(RuleDTO ruleDTO);
}
