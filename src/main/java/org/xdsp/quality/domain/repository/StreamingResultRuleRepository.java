package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StreamingResultRuleDTO;
import org.xdsp.quality.domain.entity.StreamingResultRule;

import java.util.List;

/**
 * <p>实时数据方案结果表-规则信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultRuleRepository extends BaseRepository<StreamingResultRule, StreamingResultRuleDTO>, ProxySelf<StreamingResultRuleRepository> {

    /**
     * 查看规则错误信息
     *
     * @param streamingResultRuleDTO
     * @return StreamingResultRuleDTO
     */
    List<StreamingResultRuleDTO> listResultRule(StreamingResultRuleDTO streamingResultRuleDTO);
}
