package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.AssigneeUserDTO;
import org.xdsp.quality.domain.entity.Root;

import java.util.List;

/**
 * 词根Mapper
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootMapper extends BaseMapper<Root> {
    /**
     * 条件分页查询
     * @param root
     * @return
     */
    List<Root> list(Root root);

    /**
     * 根据责任人名称去查找员工id
     * @return 员工id
     */
    Long checkCharger(String chargeName,Long tenantId);

    /**
     * 根据员工id获取部门信息
     * @param root
     * @return
     */
    List<Root> getUnitByEmployeeId(Root root);

    /**
     * 根据责任人id查询责任人信息
     * @param chargeId 责任人id 即员工id
     * @return  责任人信息
     */
    AssigneeUserDTO getAssigneeUser(Long chargeId);
}
