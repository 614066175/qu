package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
     * @param chargeDeptName 负责人部门
     */
    Long selectIdByChargeDeptName(@Param("chargeDeptName") String chargeDeptName);

    /**
     * 根据负责部门名称获取负责部门id
     * @param chargeName 负责人名称
     */
    Long selectIdByChargeName(@Param("chargeName") String chargeName);
    /**
     * 查询数据标准列表
     * @param dataStandardDTO
     * @return
     */
    List<DataStandardDTO> list(@Param("dataStandardDTO") DataStandardDTO dataStandardDTO);

    /**
     * 根据负责人名称查询负责人所在租户
     * @param chargeName
     * @return
     */
    Long selectTenantIdByChargeName(@Param("chargeName") String chargeName);

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

    List<String> selectEmployeeByChargeId(@Param("chargeId") Long chargeId);

    Long isEncrypt(Long tenantId);
}
