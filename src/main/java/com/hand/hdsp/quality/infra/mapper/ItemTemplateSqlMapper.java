package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>校验项模板SQL表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
public interface ItemTemplateSqlMapper extends BaseMapper<ItemTemplateSql> {

    /**
     * 根据条件查询SQL
     *
     * @param itemTemplateSql
     * @return
     */
    List<ItemTemplateSql> selectSql(ItemTemplateSql itemTemplateSql);
}
