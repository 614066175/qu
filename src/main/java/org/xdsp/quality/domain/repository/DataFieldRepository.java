package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.domain.entity.DataField;

import java.util.List;

/**
 * <p>字段标准表资源库</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldRepository extends BaseRepository<DataField, DataFieldDTO>, ProxySelf<DataFieldRepository> {

    /**
     * 批量导入权限方法
     *
     * @param dataFieldDTOList DataFieldDTO
     */
    boolean batchImport(List<DataFieldDTO> dataFieldDTOList);

    /**
     * 查询数据标准列表
     *
     * @param dataFieldDTO DataFieldDTO
     * @return Long
     */
    List<DataFieldDTO> list(DataFieldDTO dataFieldDTO);
}