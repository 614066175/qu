package com.hand.hdsp.quality.infra.feign.fallback;

import com.hand.hdsp.quality.api.dto.DqRuleLineDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.feign.DqRuleLineFeign;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


/**
 * ops 数据质量监控规则表 Fallback
 *
 * @author feng.liu01@hand-china.com 2020-04-20 20:03:59
 */
@Component
public class DqRuleLineFeignFallback implements DqRuleLineFeign {

    @Override
    public ResponseEntity<String> listAll(Long tenantId, DqRuleLineDTO dqRuleLineDTO) {
        throw new CommonException(ErrorCode.DQ_RULE_LINE_LIST_ALL);
    }
}
