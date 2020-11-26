package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.ApprovalLineDTO;
import com.hand.hdsp.quality.domain.entity.ApprovalLine;

/**
 * <p>申请行表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
public interface ApprovalLineRepository extends BaseRepository<ApprovalLine, ApprovalLineDTO>, ProxySelf<ApprovalLineRepository> {

}