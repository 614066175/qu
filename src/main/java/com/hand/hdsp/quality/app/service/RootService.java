package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.domain.entity.Root;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.export.vo.ExportParam;

/**
 * 词根应用服务
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootService {

    /**
     * 条件分页查询
     * @param pageRequest
     * @param root
     * @return
     */
    Page<Root> list(PageRequest pageRequest,Root root);

    /**
     * 详情
     * @param id
     * @return
     */
    Root detail(Long id);

    /**
     * 添加
     * @param root
     * @return
     */
    void create(Root root);

    /**
     * 修改
     * @param root
     * @return
     */
    void update(Root root);

    /**
     * 删除
     * @param root
     * @return
     */
    void delete(Root root);

    /**
     * 导出
     * @param root
     * @param exportParam
     * @return
     */

    List<RootGroupDTO> export(Root root, ExportParam exportParam);

    /**
     * 发布
     *
     * @param root
     */
    void publishOrOff(Root root);

    /**
     * 上线通过事件
     *
     * @param rootId
     */
    void onlineWorkflowSuccess(Long rootId);

    /**
     * 上线拒绝事件
     *
     * @param rootId
     */
    void onlineWorkflowFail(Long rootId);

    /**
     * 下线通过事件
     *
     * @param rootId
     */
    void offlineWorkflowSuccess(Long rootId);

    /**
     * 下线拒绝事件
     *
     * @param rootId
     */
    void offlineWorkflowFail(Long rootId);

    /**
     * 上线工作流审批中
     *
     * @param rootId
     */
    void onlineWorkflowing(Long rootId);

    /**
     * 下线工作流审批中
     *
     * @param rootId
     */
    void offlineWorkflowing(Long rootId);

    /**
     * 查找责任人审批
     *
     * @param rootId
     * @return
     */
    List<AssigneeUserDTO> findCharger(Long rootId);
}
