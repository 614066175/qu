package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.AssigneeUserDTO;
import com.hand.hdsp.quality.api.dto.RootGroupDTO;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.domain.entity.Root;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;

import java.util.List;
import java.util.Map;

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
     * 发布 下线
     *
     * @param root
     */
    void publishOrOff(Root root);

    /**
     * 上线回调事件
     *
     * @param rootId
     */
    void onlineWorkflowSuccess(Long rootId,String nodeApproveResult);


    /**
     * 下线回调事件
     *
     * @param rootId
     */
    void offlineWorkflowSuccess(Long rootId, String nodeApproveResult);

    /**
     * 查找责任人审批
     *
     * @param rootId
     * @return
     */
    List<AssigneeUserDTO> findCharger(Long rootId);

    /**
     * 词根申请信息
     * @param tenantId      租户
     * @param approvalId    审批id
     * @return              申请信息
     */
    StandardApprovalDTO rootApplyInfo(Long tenantId, Long approvalId);

    /**
     * 词根信息租户
     * @param approvalId    审批id
     * @return              字段标准信息
     */
    Root rootInfo(Long approvalId);

    /**
     * 词根匹配
     *
     *
     * @param tenantId
     * @param projectId
     * @param word
     * @return
     */
    String analyzerWord(Long tenantId, Long projectId, String word);

    /**
     * 词根翻译
     * @param tenantId
     * @param projectId
     * @param word
     * @return
     */
    Map<String,List<String>> rootTranslate(Long tenantId, Long projectId, String word);
}
