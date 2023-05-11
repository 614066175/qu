package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import org.apache.ibatis.annotations.Select;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>参考数据头表Mapper</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataMapper extends BaseMapper<ReferenceData> {

    /**
     * 列表查询
     * @param referenceDataDTO  查询参数
     * @return                  数据
     */
    List<ReferenceDataDTO> list(ReferenceDataDTO referenceDataDTO);

    /**
     * 根据主键查询详情
     * @param dataId    主键
     * @return          详细信息
     */
    ReferenceDataDTO detail(Long dataId);

    /**
     * 根据员工名查询员工id
     * @param employeeName  员工名
     * @param tenantId      租户
     * @return          ids
     */
    List<Long> queryEmployeeIdsByName(String employeeName, Long tenantId);

    /**
     * 格局部门名称 查询部门id
     * @param deptName  部门名称
     * @param tenantId  租户id
     * @return          ids
     */
    List<Long> queryDeptIdsByName(String deptName, Long tenantId);
}
