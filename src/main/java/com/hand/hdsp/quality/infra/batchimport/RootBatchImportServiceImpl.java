package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.domain.repository.RootLineRepository;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.mapper.RootMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.ROOT_STANDARD;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.profile.ProfileClient;
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
public class RootBatchImportServiceImpl extends BatchImportHandler implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RootMapper rootMapper;
    private final RootRepository rootRepository;
    private final RootLineRepository rootLineRepository;
    private final CommonGroupRepository commonGroupRepository;
    private final ProfileClient profileClient;

    public RootBatchImportServiceImpl(ObjectMapper objectMapper, RootMapper rootMapper, RootRepository rootRepository, RootLineRepository rootLineRepository, CommonGroupRepository commonGroupRepository, ProfileClient profileClient) {
        this.objectMapper = objectMapper;
        this.rootMapper = rootMapper;
        this.rootRepository = rootRepository;
        this.rootLineRepository = rootLineRepository;
        this.commonGroupRepository = commonGroupRepository;

        this.profileClient = profileClient;
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
                List<CommonGroup> commonGroupList = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                        .andWhere(Sqls.custom().andEqualTo(CommonGroup.FIELD_GROUP_PATH,root.getGroupPath())
                                .andEqualTo(CommonGroup.FIELD_GROUP_TYPE,ROOT_STANDARD)
                                .andEqualTo(CommonGroup.FIELD_TENANT_ID,tenantId)
                                .andEqualTo(CommonGroup.FIELD_PROJECT_ID,projectId)
                        ).build());

                if(CollectionUtils.isNotEmpty(commonGroupList)){
                    root.setGroupId(commonGroupList.get(0).getGroupId());
                }else {
                    addErrorMsg(i, String.format("未找到分组%s，请检查数据",root.getGroupPath()));
                    return false;
                }

                //责任人id
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    root.setChargeName(DataSecurityHelper.encrypt(root.getChargeName()));
                }
                Long chargeId = rootMapper.checkCharger(root.getChargeName(), root.getTenantId());
                if (ObjectUtils.isEmpty(chargeId)) {
                    addErrorMsg(i, "未找到此责任人，请检查数据");
                    return false;
                }
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
                if(ObjectUtils.isEmpty(chargeDeptId)){
                    addErrorMsg(i,"该责任人未分配部门");
                    return false;
                }
                root.setChargeDeptId(chargeDeptId);
                root.setReleaseStatus(StandardConstant.Status.CREATE);

                List<Root> rootList = rootRepository.selectByCondition(Condition.builder(Root.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(Root.FIELD_ROOT_EN_SHORT, root.getRootEnShort())
                                .andEqualTo(Root.FIELD_PROJECT_ID, root.getProjectId())
                                .andEqualTo(Root.FIELD_TENANT_ID, root.getTenantId())
                        ).build());
                //判断是更新还是插入
                if(CollectionUtils.isNotEmpty(rootList)){
                    Root rootExist = rootList.get(0);
                    //在线、下线审核中,下线要审批则报错，不要则更新，改未下线状态
                    if(StandardConstant.Status.ONLINE.equals(rootExist.getReleaseStatus())
                            || StandardConstant.Status.OFFLINE_APPROVING.equals(rootExist.getReleaseStatus())){
                        String offlineFlag = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(), null, null, WorkFlowConstant.OpenConfig.ROOT_OFFLINE);
                        if(Boolean.parseBoolean(offlineFlag)){
                            addErrorMsg(i,"词根已存在，状态不可有修改，请先下线");
                            continue;
                        }
                        root.setReleaseStatus(StandardConstant.Status.OFFLINE);
                    }else{
                        //新建、离线、发布审核中进行更新，状态不变
                        root.setReleaseStatus(rootExist.getReleaseStatus());
                    }
                    root.setId(rootExist.getId());
                    root.setObjectVersionNumber(rootExist.getObjectVersionNumber());
                    rootRepository.updateByPrimaryKeySelective(root);
                }else{
                    rootRepository.insertSelective(root);
                }


                String[] rootName =  root.getRootName().split(StandardConstant.RootName.SEPARATOR);
                List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                        .andWhere(Sqls.custom().andEqualTo(RootLine.FIELD_ROOT_ID,root.getId())
                                .andEqualTo(RootLine.FIELD_PROJECT_ID,root.getProjectId())
                                .andEqualTo(RootLine.FIELD_TENANT_ID,root.getTenantId())
                        ).build());
                if(CollectionUtils.isNotEmpty(rootLines)){
                    rootLineRepository.batchDeleteByPrimaryKey(rootLines);
                }
                rootLines = new ArrayList<>();
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
