package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.NameExecHisDetailDTO;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;

/**
 * <p>命名标准表应用服务</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameStandardService {

    /**
     * 删除标准
     *
     * @param nameStandardDTO 标准
     */
    void remove(NameStandardDTO nameStandardDTO);

    /**
     * 批量删除
     *
     * @param standardDtoList 标准列表
     */
    void bitchRemove(List<NameStandardDTO> standardDtoList);

    /**
     * 修改命名标准
     *
     * @param nameStandardDTO 命名标准
     * @return NameStandardDTO
     */
    NameStandardDTO update(NameStandardDTO nameStandardDTO);

    /**
     * 执行标准
     *
     * @param standardId 标准ID
     */
    void executeStandard(Long standardId);

    /**
     * 批量执行标准
     *
     * @param standardIdList 标准ID
     */
    void batchExecuteStandard(List<Long> standardIdList);

    /**
     * 命名标准导出
     * @param dto 命名标准
     * @param exportParam 导出参数
     * @param pageRequest 分页参数
     * @return Page<NameStandardDTO>
     */
    Page<NameStandardDTO> export(NameStandardDTO dto, ExportParam exportParam, PageRequest pageRequest);

}
