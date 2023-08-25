package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardDocDTO;
import org.xdsp.quality.domain.entity.StandardDoc;

import java.util.List;

/**
 * <p>标准文档管理表资源库</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:40:10
 */
public interface StandardDocRepository extends BaseRepository<StandardDoc, StandardDocDTO>, ProxySelf<StandardDocRepository> {

    /**
     * 批量导入标准文档方法
     *
     * @param standardDocDTOList List<StandardDocDTO>
     */
    void batchImport(List<StandardDocDTO> standardDocDTOList);
}