package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import org.apache.ibatis.annotations.Param;

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