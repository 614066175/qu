package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.AssetFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
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
     * @param dataStandardDTO
     */
    void create(DataStandardDTO dataStandardDTO);

    /**
     * 数据标准详情
     * @param tenantId
     * @param standardId
     * @return
     */
    DataStandardDTO detail(Long tenantId, Long standardId);



    /**
     * 删除数据标准
     * @param dataStandardDTO
     */
    void delete(DataStandardDTO dataStandardDTO);

    /**
     * 数据标准修改状态
     * @param dataStandardDTO
     */
    void updateStatus(DataStandardDTO dataStandardDTO);

    /**
     * 分页查询列表
     * @param pageRequest
     * @param dataStandardDTO
     * @return
     */
    Page<DataStandardDTO> list(PageRequest pageRequest, DataStandardDTO dataStandardDTO);

    /**
     * 修改数据标准
     * @param dataStandardDTO
     */
    void update(DataStandardDTO dataStandardDTO);

    /**
     * 发布数据标准
     * @param dataStandardDTO
     */
    void publishOrOff(DataStandardDTO dataStandardDTO);

    /**
     * 数据标准落标
     * @param standardAimDTOList List<StandardAimDTO>
     */
    void aim(List<StandardAimDTO> standardAimDTOList);

    /**
     * 数据标准批量关联评估方案
     * @param standardAimDTOList
     */
    void batchRelatePlan(List<StandardAimDTO> standardAimDTOList);

    /**
     * 数据标准导出
     * @param dto
     * @param exportParam
     * @param pageRequest
     * @return
     */
    List<DataStandardDTO> export(DataStandardDTO dto, ExportParam exportParam, PageRequest pageRequest);

    /**
     *
     * 标准转换为规则
     * @param standardId
     * @return
     */
    BatchPlanFieldDTO standardToRule(Long standardId);

    /**
     * 字段元数据关联标准
     * @param assetFieldDTO
     */
    void fieldAimStandard(AssetFieldDTO assetFieldDTO);
}
