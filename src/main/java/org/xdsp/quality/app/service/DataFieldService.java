package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.infra.export.dto.FieldStandardExportDTO;

import java.util.List;

/**
 * <p>字段标准表应用服务</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldService {

    /**
     * 创建字段标准
     *
     * @param dataFieldDTO
     */
    void create(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准详情
     *
     * @param tenantId
     * @param fieldId
     * @return
     */
    DataFieldDTO detail(Long tenantId, Long fieldId);

    /**
     * 删除字段标准
     *
     * @param dataFieldDTO
     */
    void delete(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准分页
     *
     * @param pageRequest
     * @param dataFieldDTO
     */
    Page<DataFieldDTO> list(PageRequest pageRequest, DataFieldDTO dataFieldDTO);

    /**
     * 条件查询字段标准集合
     * @param dataFieldDTO 查询条件
     * @return 字段标准集合
     */
    List<DataFieldDTO> findDataFieldDtoList(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准状态更新
     *
     * @param dataFieldDTO DataFieldDTO
     */
    void updateStatus(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准落标
     *
     * @param tenantId
     * @param standardAimDTOList List<StandardAimDTO>
     */
    void aim(Long tenantId, List<StandardAimDTO> standardAimDTOList, Long projectId);

    /**
     * 发布数字段标准
     *
     * @param dataFieldDTO DataFieldDTO
     */
    void publishOrOff(DataFieldDTO dataFieldDTO);

    /**
     * 数据标准导出
     *
     * @param dto
     * @param exportParam
     * @return
     */
    List<FieldStandardExportDTO> export(DataFieldDTO dto, ExportParam exportParam);

    /**
     * 上线回调事件
     * @param fieldId
     * @param nodeApproveResult
     */
    void onlineWorkflowCallback(Long fieldId,String nodeApproveResult);

    /**
     * 下线回调事件
     *
     * @param fieldId
     * @param nodeApproveResult
     */
    void offlineWorkflowCallback(Long fieldId,String nodeApproveResult);

    /**
     * 查找责任人审批
     *
     * @param tenantId
     * @param fieldId
     * @return
     */
    List<AssigneeUserDTO> findCharger(Long tenantId, Long fieldId);

    /*
     * 更新字段标准
     *
     * @param dataFieldDTO
     * @return
     */
    DataFieldDTO update(DataFieldDTO dataFieldDTO);

    /**
     * 标准转换为规则
     *
     * @param standardId
     * @param fieldType
     * @return
     */
    BatchPlanFieldDTO standardToRule(Long standardId, String fieldType);

    /**
     * 字段落标统计
     *
     * @param dataFieldDTO
     * @return
     */
    DataFieldDTO fieldAimStatistic(DataFieldDTO dataFieldDTO);

    /**
     * 字段元数据关联字段标准
     * @param assetFieldDTO     字段元数据信息
     * @param projectId         项目id
     */
    void fieldAimStandard(AssetFieldDTO assetFieldDTO, Long projectId);

    /**
     * 根据字段元数据查询字段标准
     * @param assetFieldDTO 字段元数据信息
     * @param projectId     项目id
     * @return              结果
     */
    List<DataFieldDTO> standardByField(AssetFieldDTO assetFieldDTO, Long projectId);

    /**
     * 字段标准申请信息
     * @param tenantId      租户
     * @param approvalId    审批id
     * @return              申请信息
     */
    StandardApprovalDTO fieldApplyInfo(Long tenantId, Long approvalId);

    /**
     * 字段标准信息
     * @param tenantId      租户
     * @param approvalId    审批id
     * @return              字段标准信息
     */
    DataFieldDTO fieldInfo(Long tenantId, Long approvalId);

    /**
     * 版本记录
     * @param dataFieldDTO
     */
    void doVersion(DataFieldDTO dataFieldDTO);

    /**
     * 指定字段标准修改状态，供审批中，审批结束任务状态变更
     *
     * @param tenantId
     * @param fieldId
     * @param status
     */
    void workflowing(Long tenantId, Long fieldId, String status);

    /**
     * 查询所有
     * @param dataFieldDTO
     * @return
     */
    List<DataFieldDTO> listAll(DataFieldDTO dataFieldDTO);

    /**
     * 元数据字段标准详情
     *
     * @param tenantId
     * @param fieldId
     * @param projectId
     * @return
     */
    DataFieldDTO assetDetail(Long tenantId, Long fieldId, Long projectId);
}
