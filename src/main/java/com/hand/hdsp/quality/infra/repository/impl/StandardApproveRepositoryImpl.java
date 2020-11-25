package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardApproveDTO;
import com.hand.hdsp.quality.domain.entity.StandardApprove;
import com.hand.hdsp.quality.domain.repository.StandardApproveRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准申请记录表资源库实现</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 16:49:46
 */
@Component
public class StandardApproveRepositoryImpl extends BaseRepositoryImpl<StandardApprove, StandardApproveDTO> implements StandardApproveRepository {

}
