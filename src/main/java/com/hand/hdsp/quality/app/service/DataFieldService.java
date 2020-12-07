package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;

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
     * 字段标准状态更新
     *
     * @param dataFieldDTO DataFieldDTO
     */
    void updateStatus(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准落标
     * @param standardAimDTOList List<StandardAimDTO>
     */
    void aim(List<StandardAimDTO> standardAimDTOList);

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

}
