package org.xdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.CommonGroupClient;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.domain.entity.RootLine;
import org.xdsp.quality.domain.repository.RootLineRepository;
import org.xdsp.quality.domain.repository.RootRepository;
import org.xdsp.quality.infra.constant.StandardConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.mapper.RootMapper;

import java.util.ArrayList;
import java.util.List;

import static org.xdsp.core.infra.constant.CommonGroupConstants.GroupType.ROOT_STANDARD;

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
    private final XdspProfileClient profileClient;

    @Autowired
    private CommonGroupClient commonGroupClient;

    public RootBatchImportServiceImpl(ObjectMapper objectMapper, RootMapper rootMapper, RootRepository rootRepository, RootLineRepository rootLineRepository, CommonGroupRepository commonGroupRepository, XdspProfileClient profileClient) {
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
                    //不存在直接新建
                    commonGroupClient.createGroup(tenantId, projectId, ROOT_STANDARD, root.getGroupPath());
                    CommonGroup group = commonGroupRepository.selectOne(CommonGroup.builder()
                            .groupType(ROOT_STANDARD)
                            .groupPath(root.getGroupPath())
                            .tenantId(tenantId).projectId(projectId).build());
                    root.setGroupId(group.getGroupId());
                }

                //责任人id
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    root.setChargeName(DataSecurityHelper.encrypt(root.getChargeName()));
                }
                Long chargeId = rootMapper.checkCharger(root.getChargeName(), root.getTenantId());
                if (ObjectUtils.isEmpty(chargeId)) {
                    addErrorMsg(i, "未找到此责任人，请检查数据");
                    getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                    continue;
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
                        String offlineFlag = profileClient.getProfileValue(tenantId, projectId, WorkFlowConstant.OpenConfig.ROOT_OFFLINE);
                        if(Boolean.parseBoolean(offlineFlag)){
                            addErrorMsg(i,"词根已存在，状态不可有修改，请先下线");
                            getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
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
