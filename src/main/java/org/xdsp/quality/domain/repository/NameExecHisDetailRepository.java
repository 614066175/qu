package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.NameExecHisDetailDTO;
import org.xdsp.quality.domain.entity.NameExecHisDetail;

/**
 * <p>命名标准执行历史详情表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameExecHisDetailRepository extends BaseRepository<NameExecHisDetail, NameExecHisDetailDTO>, ProxySelf<NameExecHisDetailRepository> {

}