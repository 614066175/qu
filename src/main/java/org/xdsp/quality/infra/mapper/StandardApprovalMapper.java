package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.hzero.boot.platform.plugin.hr.entity.Employee;
import org.xdsp.quality.api.dto.UserDTO;
import org.xdsp.quality.domain.entity.StandardApproval;

import java.util.List;

/**
 * <p>各种标准审批表Mapper</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
public interface StandardApprovalMapper extends BaseMapper<StandardApproval> {

    /**
     * 获取部门
     * @param employee  员工
     * @return          部门
     */
    List<String> getEmployeeUnit(Employee employee);

    /**
     * 获取user的部分信息
     * @param userId    userid
     * @return          信息
     */
    UserDTO getUserInfo(Long userId);

}
