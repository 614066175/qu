package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.ApprovalHeaderDTO;
import com.hand.hdsp.quality.domain.entity.ApprovalHeader;

/**
 * <p>申请头表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
public interface ApprovalHeaderRepository extends BaseRepository<ApprovalHeader, ApprovalHeaderDTO>, ProxySelf<ApprovalHeaderRepository> {

}