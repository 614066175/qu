package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatching;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import com.hand.hdsp.core.base.ProxySelf;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准匹配表资源库</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
public interface FieldStandardMatchingRepository extends BaseRepository<FieldStandardMatching, FieldStandardMatchingDTO>, ProxySelf<FieldStandardMatchingRepository> {
    /**
     * @param pageRequest 分页条件
     * @param fieldStandardMatchingDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    Page<FieldStandardMatchingDTO> pageFieldStandardMatching(PageRequest pageRequest, FieldStandardMatchingDTO fieldStandardMatchingDTO);
}