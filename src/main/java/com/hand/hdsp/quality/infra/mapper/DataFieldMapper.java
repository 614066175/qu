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
     * @param chargeDeptName 负责人部门
     */
    int selectIdByChargeDeptName(@Param("chargeDeptName") String chargeDeptName);

    /**
     * 根据负责部门名称获取负责部门id
     * @param chargeName 负责人名称
     */
    int selectIdByChargeName(@Param("chargeName") String chargeName);
    /**
     * 查询数据标准列表
     * @param dataFieldDTO
     * @return
     */
    List<DataFieldDTO> list(@Param("dataFieldDTO") DataFieldDTO dataFieldDTO);

}
