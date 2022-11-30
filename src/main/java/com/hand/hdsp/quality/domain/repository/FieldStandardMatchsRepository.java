package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatchs;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;
import com.hand.hdsp.core.base.ProxySelf;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准匹配记录表资源库</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
public interface FieldStandardMatchsRepository extends BaseRepository<FieldStandardMatchs, FieldStandardMatchsDTO>, ProxySelf<FieldStandardMatchsRepository> {

    Page<FieldStandardMatchsDTO> pageMatches(PageRequest pageRequest, FieldStandardMatchsDTO fieldStandardMatchsDTO);
}