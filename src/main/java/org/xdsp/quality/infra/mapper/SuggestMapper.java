package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.SuggestDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.domain.entity.Suggest;

import java.util.List;

/**
 * <p>问题知识库表Mapper</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface SuggestMapper extends BaseMapper<Suggest> {

    /**
     * 根据选项查询出来规则id
     * @param suggestDTO 查询条件
     * @return 返回值
     */
    List<SuggestDTO> selectRuleId(@Param("suggestDTO")SuggestDTO suggestDTO);

    /**
     * 根据条件进行分页查询
     * @param suggestDTO 查询条件
     * @return 返回值
     */
    List<SuggestDTO> list(@Param("suggestDTO")SuggestDTO suggestDTO);

    /**
     * 根据resultBaseId获取数据库类型
     * @param resultBaseId 查询参数
     * @return 返回值
     */
    BatchResultBase getDatasource(@Param("resultBaseId")Long resultBaseId);
}
