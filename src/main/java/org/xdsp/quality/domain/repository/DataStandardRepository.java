package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.domain.entity.DataStandard;

import java.util.List;

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
    boolean batchImport(List<DataStandardDTO> dataStandardDTOList);
}