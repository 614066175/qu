package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;
import org.xdsp.quality.api.dto.NameStandardDTO;
import org.xdsp.quality.domain.entity.NameStandard;
import org.xdsp.quality.infra.export.dto.NameStandardExportDTO;
import org.xdsp.quality.infra.vo.NameStandardDatasourceVO;
import org.xdsp.quality.infra.vo.NameStandardTableVO;

import java.util.List;

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
     * @param nameStandard 命名标准
     */
    void executeStandard(NameStandard nameStandard);

    /**
     * 批量执行标准
     *
     * @param standardIdList 标准ID
     */
    void batchExecuteStandard(List<Long> standardIdList);

    /**
     * 命名标准按分组导出
     * @param dto 命名标准
     * @param exportParam 导出参数
     * @return Page<NameStandardDTO>
     */
    List<NameStandardExportDTO> export(NameStandardDTO dto, ExportParam exportParam);

    /**
     * 获取表
     *
     * @param nameStandardDatasourceVO 数据源信息
     * @return List<NameStandardTableVO>
     */
    List<NameStandardTableVO> getTables(NameStandardDatasourceVO nameStandardDatasourceVO);

    /**
     * 创建命名标准文档
     * @param nameStandardDTO
     * @return
     */
    NameStandardDTO create(NameStandardDTO nameStandardDTO);


    /**
     * 分页查询命名标准
     *
     * @param nameStandardDTO
     * @param pageRequest
     * @return
     */
    Page<NameStandardDTO> pageNameStandards(NameStandardDTO nameStandardDTO, PageRequest pageRequest);
}
