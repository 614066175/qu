package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataFieldGroupDTO;
import com.hand.hdsp.quality.api.dto.RootDTO;
import com.hand.hdsp.quality.api.dto.RootGroupDTO;
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
}
