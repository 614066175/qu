package com.hand.hdsp.quality.infra.batchimport.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.ProjectHelper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @Title: CommonGroupImportImpl
 * @Description:
 * @author: lgl
 * @date: 2023/2/6 11:08
 */
@Slf4j
@Component
public class AbstractCommonGroupImportImpl extends BatchImportHandler implements IBatchImportService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonGroupRepository commonGroupRepository;

    @Autowired
    private CommonGroupClient commonGroupClient;


    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        boolean error = false;
        //循环导入
        for (int i = 0; i < data.size(); i++) {
            String json = data.get(i);
            try {
                CommonGroup commonGroup = objectMapper.readValue(json, CommonGroup.class);
                //判断在目标环境是否存在，若存在则跳过，不存在则新建
                String groupPath;
                if (StringUtils.isEmpty(commonGroup.getParentGroupPath())) {
                    groupPath = commonGroup.getGroupName();
                } else {
                    groupPath = String.format("%s/%s", commonGroup.getParentGroupPath(), commonGroup.getGroupName());
                }
                List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class).andWhere(Sqls.custom()
                                .andEqualTo(CommonGroup.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(CommonGroup.FIELD_PROJECT_ID, projectId)
                                .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, commonGroup.getGroupType())
                                .andEqualTo(CommonGroup.FIELD_GROUP_PATH, groupPath))
                        .build());
                if (CollectionUtils.isEmpty(commonGroups)) {
                    //如果分组不存在
                    //从头建到尾，调用封装客户端
                    commonGroupClient.createGroup(tenantId, projectId, commonGroup.getGroupType(), groupPath);
                }
                getContextList().get(i).setDataStatus(DataStatus.IMPORT_SUCCESS);
            } catch (Exception e) {
                error = true;
                log.error("导入分组失败", e);
                addErrorMsg(i, "导入分组失败:" + e.getMessage());
                getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
            }
        }
        return !error;
    }
}
