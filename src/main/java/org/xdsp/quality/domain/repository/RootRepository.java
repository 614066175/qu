package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.AssigneeUserDTO;
import org.xdsp.quality.api.dto.RootDTO;
import org.xdsp.quality.domain.entity.Root;

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
