package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>校验项模板SQL表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ItemTemplateSqlDO extends AuditDomain {

    private Long sqlId;

    private String checkItem;

    private String datasourceType;

    private String sqlContent;

    private Integer enabledFlag;

    private Long tenantId;

}