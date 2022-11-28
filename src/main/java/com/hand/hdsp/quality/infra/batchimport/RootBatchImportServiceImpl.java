package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.RootLineRepository;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.RootMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.ROOT;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/22 20:11
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_ROOT,sheetIndex = 1)
public class RootBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RootMapper rootMapper;
    private final RootRepository rootRepository;
    private final RootService rootService;
    private final RootLineRepository rootLineRepository;
    private final StandardGroupRepository standardGroupRepository;

    public RootBatchImportServiceImpl(ObjectMapper objectMapper, RootMapper rootMapper, RootRepository rootRepository, RootService rootService, RootLineRepository rootLineRepository, StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.rootMapper = rootMapper;
        this.rootRepository = rootRepository;
        this.rootService = rootService;
        this.rootLineRepository = rootLineRepository;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try{
            for(int i=0;i<data.size();i++){
                Root root = objectMapper.readValue(data.get(i), Root.class);
                root.setTenantId(tenantId);
                root.setProjectId(projectId);

                //分组id
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, root.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, ROOT)
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                ).build());
                if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
                    root.setGroupId(standardGroupDTOList.get(0).getGroupId());
                }

                //责任人id
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    root.setChargeName(DataSecurityHelper.encrypt(root.getChargeName()));
                }
                Long chargeId = rootMapper.checkCharger(root.getChargeName(), root.getTenantId());
                root.setChargeId(chargeId);

                //获取责任部门id
                List<Root> unitList = rootMapper.getUnitByEmployeeId(root);
                Long chargeDeptId = null;
                if(StringUtils.isNotEmpty(root.getChargeDept())){
                    if(DataSecurityHelper.isTenantOpen()){
                        root.setChargeDept(DataSecurityHelper.encrypt(root.getChargeDept()));
                    }
                    for(Root tmp:unitList){
                        if(root.getChargeDept().equals(tmp.getChargeDept())){
                            chargeDeptId = tmp.getChargeDeptId();
                        }
                    }
                }else{
                    chargeDeptId = unitList.get(0).getChargeDeptId();
                }
                root.setChargeDeptId(chargeDeptId);
                root.setReleaseStatus(StandardConstant.Status.CREATE);
                rootRepository.insertSelective(root);

                String[] rootName =  root.getRootName().split(StandardConstant.RootName.SEPARATOR);
                List<RootLine> rootLines = new ArrayList<>();
                RootLine rootLine;
                for(int j=0;j<rootName.length;j++){
                    rootLine = RootLine.builder()
                            .rootId(root.getId())
                            .rootName(rootName[j])
                            .projectId(projectId)
                            .tenantId(tenantId)
                            .build();
                    rootLines.add(rootLine);
                }
                rootLineRepository.batchInsertSelective(rootLines);
                rootService.publishOrOff(root);
            }
        }catch (Exception e){
            // 失败
            log.error("Root Object data:{}", data);
            log.error("Root Object Read Json Error", e);
            return false;
        }
        return true;
    }
}
