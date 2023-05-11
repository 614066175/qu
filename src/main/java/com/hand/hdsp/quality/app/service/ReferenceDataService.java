package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.infra.export.dto.ReferenceDataExportDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.export.vo.ExportParam;

/**
 * <p>参考数据头表应用服务</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataService {

    /**
     * 列表查询
     * @param projectId         项目ID
     * @param tenantId          租户ID
     * @param referenceDataDTO  查询参数
     * @param pageRequest       分页参数
     * @return                  列表
     */
    Page<ReferenceDataDTO> list(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO, PageRequest pageRequest);

    /**
     * 根据主键查询详情
     * @param dataId    主键
     * @return          详情
     */
    ReferenceDataDTO detail(Long dataId);

    /**
     * 参考数据头插入
     * @param projectId         项目ID
     * @param tenantId          租户ID
     * @param referenceDataDTO  referenceDataDTO
     */
    void create(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO);

    /**
     * 修改
     * @param projectId         项目ID
     * @param tenantId          租户ID
     * @param referenceDataDTO  referenceDataDTO
     */
    void update(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO);

    /**
     * 删除
     * @param projectId         项目ID
     * @param tenantId          租户ID
     * @param referenceDataDTO  referenceDataDTO
     */
    void remove(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO);

    /**
     * 批量删除
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataDTOList  referenceDataDTOList
     */
    void batchRemove(Long projectId, Long tenantId, List<ReferenceDataDTO> referenceDataDTOList);

    /**
     * 发布参考数据
     * @param projectId         项目ID
     * @param tenantId          租户ID
     * @param referenceDataDTO  referenceDataDTO
     */
    void release(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO);

    /**
     * 批量发布参考数据
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataDTOList  referenceDataDTOList
     */
    void batchRelease(Long projectId, Long tenantId, List<ReferenceDataDTO> referenceDataDTOList);

    /**
     * 下线参考数据
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataDTO      referenceDataDTO
     */
    void offline(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO);

    /**
     * 批量下线参考数据
     * @param projectId             项目ID
     * @param tenantId              租户ID
     * @param referenceDataDTOList  referenceDataDTOList
     */
    void batchOffline(Long projectId, Long tenantId, List<ReferenceDataDTO> referenceDataDTOList);

    /**
     * 发布回调接口
     * @param tenantId          租户ID
     * @param recordId          审批记录表ID
     * @param nodeApproveResult 审批结果
     */
    void releaseCallback(Long tenantId, Long recordId, String nodeApproveResult);

    /**
     * 下线回调接口
     * @param tenantId          租户ID
     * @param recordId          审批记录表ID
     * @param nodeApproveResult 审批结果
     */
    void offlineCallback(Long tenantId, Long recordId, String nodeApproveResult);

    /**
     * 工作流撤回接口
     * @param tenantId          租户ID
     * @param recordId          工作流记录表
     */
    void withdrawEvent(Long tenantId, Long recordId);

    /**
     * 导出参考数据
     * @param projectId         项目id
     * @param tenantId          租户id
     * @param referenceDataDTO  查询条件
     * @param exportParam       导出参数
     * @return                  要导出的数据
     */
    List<ReferenceDataExportDTO> export(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO, ExportParam exportParam);
}
