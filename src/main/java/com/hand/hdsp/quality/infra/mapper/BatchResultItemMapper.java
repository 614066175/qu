package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>批数据方案结果表-校验项信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
public interface BatchResultItemMapper extends BaseMapper<BatchResultItem> {


    /**
     * 查询历史实际值
     *
     * @param batchResultItem
     * @return
     */
    List<BatchResultItemDO> queryList(BatchResultItemDO batchResultItem);
}
