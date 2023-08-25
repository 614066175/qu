package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameExecHisDetailDTO;
import org.xdsp.quality.domain.entity.NameExecHisDetail;
import org.xdsp.quality.domain.repository.NameExecHisDetailRepository;

/**
 * <p>命名标准执行历史详情表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameExecHisDetailRepositoryImpl extends BaseRepositoryImpl<NameExecHisDetail, NameExecHisDetailDTO> implements NameExecHisDetailRepository {

}
