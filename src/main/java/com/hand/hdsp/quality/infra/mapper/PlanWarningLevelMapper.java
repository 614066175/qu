package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>方案告警等级表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanWarningLevelMapper extends BaseMapper<PlanWarningLevel> {

    /**
     * 删除
     *
     * @param sourceId
     * @param sourceType
     * @return
     */
    @Delete("delete from xqua_plan_warning_level where source_id = #{sourceId} and source_type = #{sourceType}")
    int deleteByParentId(@Param("sourceId") Long sourceId, @Param("sourceType") String sourceType);
}
