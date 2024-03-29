package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ItemTemplateSqlDTO;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import org.xdsp.quality.infra.mapper.ItemTemplateSqlMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>校验项模板SQL表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
@Component
public class ItemTemplateSqlRepositoryImpl extends BaseRepositoryImpl<ItemTemplateSql, ItemTemplateSqlDTO> implements ItemTemplateSqlRepository {

    private final ItemTemplateSqlMapper itemTemplateSqlMapper;

    public ItemTemplateSqlRepositoryImpl(ItemTemplateSqlMapper itemTemplateSqlMapper) {
        this.itemTemplateSqlMapper = itemTemplateSqlMapper;
    }

    @Override
    public ItemTemplateSql selectSql(ItemTemplateSql itemTemplateSql) {
        //先根据数据源类型查询，没有则再查询_ALL的
        List<ItemTemplateSql> itemTemplateSqls =
                itemTemplateSqlMapper.selectSql(itemTemplateSql);
        if (itemTemplateSqls.size() > 1) {
            //有自己的逻辑
            List<ItemTemplateSql> collect = itemTemplateSqls.stream()
                    .filter(sql -> sql.getDatasourceType().equals(itemTemplateSql.getDatasourceType()))
                    .collect(Collectors.toList());
            return collect.get(0);
        }
        return itemTemplateSqls.get(0);
    }
}
