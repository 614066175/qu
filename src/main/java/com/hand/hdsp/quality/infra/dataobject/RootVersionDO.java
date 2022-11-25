package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>词根版本数据对象</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RootVersionDO extends AuditDomain {

    private Long id;

    private Long groupId;

    private Long rootId;

    private String rootEnShort;

    private String rootName;

    private String rootEn;

    private String rootDesc;

    private Long chargeDeptId;

    private Long chargeId;

    private Long versionNumber;

    private Long tenantId;

    private Long projectId;

}
