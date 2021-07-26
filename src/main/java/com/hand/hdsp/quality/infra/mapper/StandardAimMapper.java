package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.domain.entity.StandardAim;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>标准落标表Mapper</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
public interface StandardAimMapper extends BaseMapper<StandardAim> {

    /**
     * 根据筛选条件进行模糊查询
     * @param standardAimDTO 标准落标表实体
     * @return 返回查询的结果list
     */
    List<StandardAimDTO> selectByConditionLike(@Param("standardAimDTO") StandardAimDTO standardAimDTO);

}
