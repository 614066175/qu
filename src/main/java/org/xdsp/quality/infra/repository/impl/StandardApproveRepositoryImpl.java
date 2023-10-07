package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardApproveDTO;
import org.xdsp.quality.domain.entity.StandardApprove;
import org.xdsp.quality.domain.repository.StandardApproveRepository;

/**
 * <p>标准申请记录表资源库实现</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 16:49:46
 */
@Component
public class StandardApproveRepositoryImpl extends BaseRepositoryImpl<StandardApprove, StandardApproveDTO> implements StandardApproveRepository {

}
