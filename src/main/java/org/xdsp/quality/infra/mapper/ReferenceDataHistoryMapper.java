package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.ReferenceDataHistoryDTO;
import org.xdsp.quality.domain.entity.ReferenceDataHistory;

import java.util.List;

/**
 * <p>参考数据头表Mapper</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataHistoryMapper extends BaseMapper<ReferenceDataHistory> {

    /**
     * 获取最大版本号
     * @param dataId    参考数据ID
     * @return          结果
     */
    Long queryMaxVersion(Long dataId);

    /**
     * 根据dataId 查询版本
     * @param dataId    dataId
     * @return          结果
     */
    List<ReferenceDataHistoryDTO> queryByDataId(Long dataId);

    /**
     * 根据主键查询详情
     * @param historyId 主键
     * @return          结果
     */
    ReferenceDataHistoryDTO detail(Long historyId);
}
