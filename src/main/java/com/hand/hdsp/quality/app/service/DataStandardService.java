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
     * 查询并解密获取数据标准集合信息
     * @param dataStandardDTO
     * @return
     */
    List<DataStandardDTO> findDataStandards(DataStandardDTO dataStandardDTO);

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
     * 从分组导出数据标准
     *
     * @param dto 导出的查询参数
     * @param exportParam 导出参数
     * @return 分组的数据标准
     */
    List<DataStandardGroupDTO> export(DataStandardDTO dto, ExportParam exportParam);

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
    void startWorkFlow(String workflowKey, DataStandardDTO dataStandardDTO, String status);

    /**
     * 查找标准审批人
     * @param tenantId
     * @param dataStandardCode
     * @return
     */
    List<AssigneeUserDTO> findCharger(Long tenantId, String dataStandardCode);

    /**
     * 上线回调事件
     * @param dataStandardCode
     * @param nodeApproveResult
     */
    void onlineWorkflowCallback(String dataStandardCode,String nodeApproveResult);

    /**
     * 下线回调事件
     * @param dataStandardCode
     * @param nodeApproveResult
     */
    void offlineWorkflowCallback(String dataStandardCode,String nodeApproveResult);

    /**
     * 数据标准审批申请信息
     * @param tenantId      租户id
     * @param approvalId    审批记录id
     * @return              审批信息
     */
    StandardApprovalDTO dataApplyInfo(Long tenantId, Long approvalId);

    /**
     * 数据标准详细信息
     * @param tenantId      租户Id
     * @param approvalId    审批记录id
     * @return              详细信息
     */
    DataStandardDTO dataInfo(Long tenantId, Long approvalId);

    /**
     * 版本记录
     * @param dataStandardDTO
     */
    void doVersion(DataStandardDTO dataStandardDTO);

    /**
     * 发布时处理关联了评估的落标
     *
     * @param aimDTOS List<StandardAimDTO>
     */
    void publishRelatePlan(List<StandardAimDTO> aimDTOS);

    /**
     * 指定数据标准修改状态，供审批中，审批结束任务状态变更
     *
     * @param tenantId
     * @param dataStandardCode
     * @param status
     */
    void workflowing(Long tenantId, String dataStandardCode, String status);
}
