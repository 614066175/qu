package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.infra.dataobject.BatchResultBaseDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>批数据方案结果表-表信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultBaseMapper extends BaseMapper<BatchResultBase> {

    /**
     * 查询表大小和表行数
     *
     * @param batchResultBase
     * @return
     */
    List<BatchResultBaseDO> queryList(BatchResultBaseDO batchResultBase);

}
