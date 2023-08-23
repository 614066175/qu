package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ItemTemplateSqlDTO;
import org.xdsp.quality.domain.entity.ItemTemplateSql;

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
