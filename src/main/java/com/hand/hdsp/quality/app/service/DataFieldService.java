package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;

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
     * 字段标准状态更新
     *
     * @param dataFieldDTO DataFieldDTO
     */
    void updateStatus(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准落标
     * @param tenantId
     * @param standardAimDTOList List<StandardAimDTO>
     */
    void aim(Long tenantId,List<StandardAimDTO> standardAimDTOList, Long projectId);

    /**
     * 发布数字段标准
     * @param dataFieldDTO DataFieldDTO
     */
    void publishOrOff(DataFieldDTO  dataFieldDTO);

    /**
     * 数据标准导出
     * @param dto
     * @param exportParam
     * @param pageRequest
     * @return
     */
    List<DataFieldDTO> export(DataFieldDTO dto, ExportParam exportParam, PageRequest pageRequest);

    /**
     * 上线通过事件
     * @param tenantId
     * @param fieldId
     */
    void onlineWorkflowSuccess(Long tenantId, Long fieldId);

    /**
     * 上线拒绝事件
     * @param tenantId
     * @param fieldId
     */
    void onlineWorkflowFail(Long tenantId, Long fieldId);

    /**
     * 下线通过事件
     * @param tenantId
     * @param fieldId
     */
    void offlineWorkflowSuccess(Long tenantId, Long fieldId);

    /**
     * 上线拒绝事件
     * @param tenantId
     * @param fieldId
     */
    void offlineWorkflowFail(Long tenantId, Long fieldId);

    /**
     * 上线工作流审批中
     * @param tenantId
     * @param fieldId
     */
    void onlineWorkflowing(Long tenantId, Long fieldId);

    /**
     * 下线工作流审批中
     * @param tenantId
     * @param fieldId
     */
    void offlineWorkflowing(Long tenantId, Long fieldId);

    /**
     * 查找责任人审批
     * @param tenantId
     * @param fieldId
     * @return
     */
    List<AssigneeUserDTO> findCharger(Long tenantId, Long fieldId);

    /**
     * 字段落标统计
     * @param standardAimDTO
     * @return
     */
    FieldOverView fieldAimCount(StandardAimDTO standardAimDTO) throws InterruptedException;
}
