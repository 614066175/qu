package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatchs;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>字段标准匹配记录表Mapper</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
public interface FieldStandardMatchsMapper extends BaseMapper<FieldStandardMatchs> {

    List<FieldStandardMatchsDTO> listAll(FieldStandardMatchsDTO fieldStandardMatchsDTO);
}
