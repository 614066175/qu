package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.ItemTemplateSqlDTO;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;

/**
 * <p>校验项模板SQL表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
public interface ItemTemplateSqlRepository extends BaseRepository<ItemTemplateSql, ItemTemplateSqlDTO>, ProxySelf<ItemTemplateSqlRepository> {

    /**
     * 根据条件查询SQL
     *
     * @param itemTemplateSql
     * @return
     */
    ItemTemplateSql selectSql(ItemTemplateSql itemTemplateSql);
}
