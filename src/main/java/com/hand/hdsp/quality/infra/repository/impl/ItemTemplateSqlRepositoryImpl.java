package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.ItemTemplateSqlDTO;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.mapper.ItemTemplateSqlMapper;
import org.springframework.stereotype.Component;

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
        return itemTemplateSqlMapper.selectSql(itemTemplateSql);
    }
}
