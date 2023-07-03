package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchResultBaseDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;

import java.util.List;

/**
 * <p>批数据方案结果表-表信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultBaseRepository extends BaseRepository<BatchResultBase, BatchResultBaseDTO>, ProxySelf<BatchResultBaseRepository> {

    /**
     * 分页查看评估报告结果base表信息
     *
     * @param batchResultBaseDTO
     * @return
     */
    Page<BatchResultBaseDTO> listResultBase(PageRequest pageRequest, BatchResultBaseDTO batchResultBaseDTO);

    /**
     * 查看所有
     * @param batchResultBaseDTO
     * @return
     */
    List<BatchResultBaseDTO> listResultBaseAll(BatchResultBaseDTO batchResultBaseDTO);

    /**
     * 根据planBaseId,projectId获取最大结果Id
     *
     * @param planBaseId
     * @param projectId 查询条件
     * @return 返回值
     */
    Long selectMaxResultBaseId(Long planBaseId,Long projectId);
}
