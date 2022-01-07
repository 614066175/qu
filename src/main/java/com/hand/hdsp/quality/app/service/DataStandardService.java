package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:38
 * @since 1.0
 */
public interface DataStandardService {

    /**
     * 创建数据标准
     *
     * @param dataStandardDTO
     */
    void create(DataStandardDTO dataStandardDTO);

    /**
     * 数据标准详情
     *
     * @param tenantId
     * @param standardId
     * @return
     */
    DataStandardDTO detail(Long tenantId, Long standardId);


    /**
     * 删除数据标准
     *
     * @param dataStandardDTO
     */
    void delete(DataStandardDTO dataStandardDTO);

    /**
     * 数据标准修改状态
     *
     * @param dataStandardDTO
     */
    void updateStatus(DataStandardDTO dataStandardDTO);

    /**
     * 分页查询列表
     *
     * @param pageRequest
     * @param dataStandardDTO
     * @return
     */
    Page<DataStandardDTO> list(PageRequest pageRequest, DataStandardDTO dataStandardDTO);

    /**
     * 修改数据标准
     *
     * @param dataStandardDTO
     */
    void update(DataStandardDTO dataStandardDTO);

    /**
     * 发布数据标准
     *
     * @param dataStandardDTO
     */
    void publishOrOff(DataStandardDTO dataStandardDTO);

    /**
     * 数据标准落标
     *
     * @param standardAimDTOList List<StandardAimDTO>
     */
    void aim(Long tenantId, List<StandardAimDTO> standardAimDTOList, Long projectId);

    /**
     * 数据标准批量关联评估方案
     *
     * @param standardAimDTOList
     */
    void batchRelatePlan(Long tenantId, List<StandardAimDTO> standardAimDTOList,Long projectId);

    /**
     * 数据标准导出
     *
     * @param dto
     * @param exportParam
     * @param pageRequest
     * @return
     */
    List<DataStandardDTO> export(DataStandardDTO dto, ExportParam exportParam, PageRequest pageRequest);

    /**
     * 标准转换为规则
     *
     * @param standardId
     * @return
     */
    BatchPlanFieldDTO standardToRule(Long standardId);

    /**
     * 字段元数据关联标准
     *
     * @param assetFieldDTO
     */
    void fieldAimStandard(AssetFieldDTO assetFieldDTO, Long projectId);

    /**
     * @param assetFieldDTO
     * @return
     */
    List<DataStandardDTO> standardByField(AssetFieldDTO assetFieldDTO, Long projectId);

    /**
     * 元数据数据标准详情
     *
     * @param tenantId
     * @param standardId
     * @param projectId
     * @return
     */
    DataStandardDTO assetDetail(Long tenantId, Long standardId, Long projectId);

    /**
     * 开启工作流
     * @param workflowKey
     * @param dataStandardDTO
     */
    void startWorkFlow(String workflowKey, DataStandardDTO dataStandardDTO);

    /**
     * 查找标准审批人
     * @param tenantId
     * @param dataStandardCode
     * @return
     */
    List<AssigneeUserDTO> findCharger(Long tenantId, String dataStandardCode);

    /**
     * 上线通过事件
     * @param tenantId
     * @param dataStandardCode
     */
    void onlineWorkflowSuccess(Long tenantId, String dataStandardCode);

    /**
     * 上线拒绝事件
     * @param tenantId
     * @param dataStandardCode
     */
    void onlineWorkflowFail(Long tenantId, String dataStandardCode);

    /**
     * 下线通过事件
     * @param tenantId
     * @param dataStandardCode
     */
    void offlineWorkflowSuccess(Long tenantId, String dataStandardCode);

    /**
     * 上线拒绝事件
     * @param tenantId
     * @param dataStandardCode
     */
    void offlineWorkflowFail(Long tenantId, String dataStandardCode);

    /**
     * 上线工作流审批中
     * @param tenantId
     * @param dataStandardCode
     */
    void onlineWorkflowing(Long tenantId, String dataStandardCode);

    /**
     * 下线工作流审批中
     * @param tenantId
     * @param dataStandardCode
     */
    void offlineWorkflowing(Long tenantId, String dataStandardCode);



}
