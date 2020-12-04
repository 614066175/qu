package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;

/**
 * <p>数据标准表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface DataStandardRepository extends BaseRepository<DataStandard, DataStandardDTO>, ProxySelf<DataStandardRepository> {



    /**
     * 批量导入权限方法
     *
     * @param dataStandardDTOList DataStandardDTO
     */
    void batchImport(List<DataStandardDTO> dataStandardDTOList);
}