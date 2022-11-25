package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.AssigneeUserDTO;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.api.dto.RootDTO;
import com.hand.hdsp.core.base.ProxySelf;

import java.util.List;

/**
 * <p>词根资源库</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
public interface RootRepository extends BaseRepository<Root, RootDTO>, ProxySelf<RootRepository> {
    /**
     * 条件分页查询
     * @param root
     * @return
     */
    List<Root> list(Root root);

    /**
     * 根据责任人id查询责任人信息
     * @param chargeId 责任人id 即员工id
     * @return  责任人信息
     */
    AssigneeUserDTO getAssigneeUser(Long chargeId);
}