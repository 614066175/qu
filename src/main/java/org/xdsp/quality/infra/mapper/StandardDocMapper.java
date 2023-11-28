package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.StandardDocDTO;
import org.xdsp.quality.domain.entity.StandardDoc;

import java.util.List;

/**
 * <p>标准文档管理表Mapper</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:40:10
 */
public interface StandardDocMapper extends BaseMapper<StandardDoc> {


    /**
     * 查询标准文档列表
     *
     * @param standardDocDTO StandardDocDTO
     * @return List<StandardDocDTO>
     */
    List<StandardDocDTO> list(@Param("standardDocDTO") StandardDocDTO standardDocDTO);


    /**
     *
     * @param userId
     * @return
     */
    String selectUserNameById(Long userId);

    /**
     * 更新注释为null
     * @param standardDocDTO 更新条件
     * @return 返回值
     */
    Integer updateDesc(@Param("standardDocDTO") StandardDocDTO standardDocDTO);

    /**
     * 根据负责部门名称获取负责人部门id
     *
     * @param chargeDeptName 负责人部门
     * @return Long
     */
    List<Long> selectIdByChargeDeptName(@Param("chargeDeptName") String chargeDeptName, @Param("tenantId") Long tenantId);

    /**
     * 根据负责人名称获取负责部门id
     *
     * @param chargeName 负责人名称
     * @return Long
     */
    List<Long> selectIdByChargeName(@Param("chargeName") String chargeName, @Param("tenantId") Long tenantId);
    /**
     * 根据责任人名称去查找员工id
     * @return 员工id
     */
    List<Long> checkCharger(String chargeName, Long tenantId);
}
