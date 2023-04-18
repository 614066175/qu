package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.ReferenceDataHistoryDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>参考数据头表应用服务</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataHistoryService {

    /**
     * 列表查询
     * @param projectId                 项目ID
     * @param tenantId                  租户ID
     * @param referenceDataHistoryDTO   referenceDataHistoryDTO
     * @param pageRequest               分页参数
     * @return
     */
    Page<ReferenceDataHistoryDTO> list(Long projectId, Long tenantId, ReferenceDataHistoryDTO referenceDataHistoryDTO, PageRequest pageRequest);

    /**
     * 根据历史主键查询详情
     * @param historyId 主键
     * @return          详情
     */
    ReferenceDataHistoryDTO detail(Long historyId);

    /**
     * 删除版本
     * @param projectId                 项目ID
     * @param tenantId                  租户ID
     * @param referenceDataHistoryDTO   referenceDataHistoryDTO
     */
    void remove(Long projectId, Long tenantId, ReferenceDataHistoryDTO referenceDataHistoryDTO);

}
