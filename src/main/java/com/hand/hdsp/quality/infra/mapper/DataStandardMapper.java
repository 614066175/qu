package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.AssigneeUserDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.User;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:32
 * @since 1.0
 */
public interface DataStandardMapper extends BaseMapper<DataStandard> {

    /**
     * 根据负责人名称获取负责人id
     *
     * @param chargeDeptName 负责人部门
     */
    List<Long> selectIdByChargeDeptName(@Param("chargeDeptName") String chargeDeptName, @Param("tenantId") Long tenantId);

    /**
     * 根据负责部门名称获取负责部门id
     *
     * @param chargeName 负责人名称
     */
    List<Long> selectIdByChargeName(@Param("chargeName") String chargeName, @Param("tenantId") Long tenantId);

    /**
     * 查询数据标准列表
     *
     * @param dataStandardDTO
     * @return
     */
    List<DataStandardDTO> list(@Param("dataStandardDTO") DataStandardDTO dataStandardDTO);


    /**
     * @param chargeId
     * @return
     */
    String selectChargeNameById(@Param("chargeId") Long chargeId);

    /**
     * @param chargeDeptId
     * @return
     */
    String selectChargeDeptNameById(@Param("chargeDeptId") Long chargeDeptId);

    /**
     * 根据责任人id查询责任人信息
     * @param chargeId 责任人id 即员工id
     * @return  责任人信息
     */
    AssigneeUserDTO selectAssigneeUser(@Param("chargeId") Long chargeId);
}
