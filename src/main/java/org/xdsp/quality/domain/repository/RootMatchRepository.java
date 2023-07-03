package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.RootMatchDTO;
import org.xdsp.quality.domain.entity.RootMatch;

/**
 * <p>字段标准匹配表资源库</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
public interface RootMatchRepository extends BaseRepository<RootMatch, RootMatchDTO>, ProxySelf<RootMatchRepository> {

    /**
     * @param pageRequest 分页条件
     * @param rootMatchDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    Page<RootMatchDTO> pageRootMatchDTOList(PageRequest pageRequest, RootMatchDTO rootMatchDTO);

}