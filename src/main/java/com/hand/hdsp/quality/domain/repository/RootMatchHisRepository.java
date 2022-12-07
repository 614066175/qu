package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.RootMatchHis;
import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import com.hand.hdsp.core.base.ProxySelf;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准匹配记录表资源库</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
public interface RootMatchHisRepository extends BaseRepository<RootMatchHis, RootMatchHisDTO>, ProxySelf<RootMatchHisRepository> {
    /**
     * 字段标准匹配分页记录
     * @param pageRequest 分页参数
     * @param rootMatchHisDTO 查询条件
     * @return 字段标准匹配分页记录
     */
    Page<RootMatchHisDTO> pageMatches(PageRequest pageRequest, RootMatchHisDTO rootMatchHisDTO);
}