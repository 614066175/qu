package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>字段标准表Mapper</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldMapper extends BaseMapper<DataField> {

    /**
     * 根据负责人名称获取负责人id
     *
     * @param chargeDeptName 负责人部门
     * @return Long
     */
    List<Long> selectIdByChargeDeptName(@Param("chargeDeptName") String chargeDeptName, @Param("tenantId") Long tenantId);

    /**
     * 根据负责部门名称获取负责部门id
     *
     * @param chargeName 负责人名称
     * @return Long
     */
    List<Long> selectIdByChargeName(@Param("chargeName") String chargeName, @Param("tenantId") Long tenantId);

    /**
     * 查询数据标准列表
     *
     * @param dataFieldDTO DataFieldDTO
     * @return Long
     */
    List<DataFieldDTO> list(@Param("dataFieldDTO") DataFieldDTO dataFieldDTO);


    /**
     * 根据负责人名称查询负责人ID
     *
     * @param chargeId 负责人ID
     * @return String
     */
    String selectChargeNameById(@Param("chargeId") Long chargeId);

    /**
     * 根据部门名称查询部门ID
     *
     * @param chargeDeptId 部门ID
     * @return String
     */
    String selectChargeDeptNameById(@Param("chargeDeptId") Long chargeDeptId);
}
