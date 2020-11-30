package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameExecHisDetail;
import com.hand.hdsp.quality.api.dto.NameExecHisDetailDTO;
import com.hand.hdsp.quality.domain.repository.NameExecHisDetailRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准执行历史详情表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameExecHisDetailRepositoryImpl extends BaseRepositoryImpl<NameExecHisDetail, NameExecHisDetailDTO> implements NameExecHisDetailRepository {

}
