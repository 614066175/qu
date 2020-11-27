package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
}
