package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>参考数据值应用服务</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataValueService {

    /**
     * 参考值列表查询
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataValueDTO 查询参数
     * @param pageRequest           分页参数
     * @return                      列表数据
     */
    Page<ReferenceDataValueDTO> list(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO, PageRequest pageRequest);

    /**
     * 新增
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataValueDTO referenceDataValueDTO
     */
    void create(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO);

    /**
     * 批量新增
     * @param projectId                 项目ID
     * @param tenantId                  租户ID
     * @param referenceDataValueDTOList referenceDataValueDTOList
     */
    void batchCreate(Long projectId, Long tenantId, List<ReferenceDataValueDTO> referenceDataValueDTOList);

    /**
     * 更新
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataValueDTO referenceDataValueDTO
     */
    void update(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO);

    /**
     * 批量更新
     * @param projectId                 项目ID
     * @param tenantId                  租户ID
     * @param referenceDataValueDTOList referenceDataValueDTOList
     */
    void batchUpdate(Long projectId, Long tenantId, List<ReferenceDataValueDTO> referenceDataValueDTOList);

    /**
     * 删除
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataValueDTO referenceDataValueDTO
     */
    void remove(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO);

    /**
     * 批量删除
     * @param projectId                 项目ID
     * @param tenantId                  租户ID
     * @param referenceDataValueDTOList referenceDataValueDTOList
     */
    void batchRemove(Long projectId, Long tenantId, List<ReferenceDataValueDTO> referenceDataValueDTOList);
}
