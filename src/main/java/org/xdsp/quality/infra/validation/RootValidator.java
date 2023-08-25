package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.domain.entity.RootLine;
import org.xdsp.quality.domain.repository.RootLineRepository;
import org.xdsp.quality.domain.repository.RootRepository;
import org.xdsp.quality.infra.constant.StandardConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.mapper.RootMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/22 20:53
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_ROOT, sheetIndex = 1)})
public class RootValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final RootMapper rootMapper;
    private final RootRepository rootRepository;
    private final RootLineRepository rootLineRepository;
    private final ProfileClient profileClient;

    public RootValidator(ObjectMapper objectMapper, RootMapper rootMapper, RootRepository rootRepository, RootLineRepository rootLineRepository, ProfileClient profileClient) {
        this.objectMapper = objectMapper;
        this.rootMapper = rootMapper;
        this.rootRepository = rootRepository;
        this.rootLineRepository = rootLineRepository;
        this.profileClient = profileClient;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        Set<String> rootNameSet = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            try {
                Root root = objectMapper.readValue(data.get(i), Root.class);
                root.setTenantId(tenantId);
                root.setProjectId(projectId);

                List<Root> rootList = rootRepository.selectByCondition(Condition.builder(Root.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(Root.FIELD_ROOT_EN_SHORT, root.getRootEnShort())
                                .andEqualTo(Root.FIELD_PROJECT_ID, root.getProjectId())
                                .andEqualTo(Root.FIELD_TENANT_ID, root.getTenantId())
                        ).build());

                //词根中文校验,除自身外
                Long rootId = -1L;
                if(CollectionUtils.isNotEmpty(rootList)){
                    rootId = rootList.get(0).getId();
                }
                String[] rootName = root.getRootName().split(StandardConstant.RootName.SEPARATOR);
                List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                        .andWhere(Sqls.custom().andIn(RootLine.FIELD_ROOT_NAME, Arrays.asList(rootName))
                                .andEqualTo(RootLine.FIELD_PROJECT_ID, root.getProjectId())
                                .andEqualTo(RootLine.FIELD_TENANT_ID, root.getTenantId())
                                .andNotEqualTo(RootLine.FIELD_ROOT_ID, rootId)
                        ).build());
                StringBuffer rootNameStr = new StringBuffer();
                if (CollectionUtils.isNotEmpty(rootLines)) {
                    for (RootLine tmp : rootLines) {
                        rootNameStr.append(tmp.getRootName()).append(" ");
                    }
                    addErrorMsg(i, String.format("词根中文名【%s】已被其他词根引用!", rootNameStr));
                }

                //校验表中词根中文重复
                rootNameStr = new StringBuffer();
                for (int j = 0; j < rootName.length; j++) {
                    if (!rootNameSet.add(rootName[j])) {
                        rootNameStr.append(rootName[j]).append(" ");
                    }
                }
                if (StringUtils.isNotEmpty(rootNameStr)) {
                    addErrorMsg(i, String.format("词根中文名【%s】导入文件中重复存在!", rootNameStr));
                }

                //校验的责任人名称为员工姓名
                if (DataSecurityHelper.isTenantOpen()) {
                    //加密后查询
                    root.setChargeName(DataSecurityHelper.encrypt(root.getChargeName()));
                }
                Long chargeId = rootMapper.checkCharger(root.getChargeName(), root.getTenantId());
                if (ObjectUtils.isEmpty(chargeId)) {
                    addErrorMsg(i, "未找到此责任人，请检查数据");
                    continue;
                }
                root.setChargeId(chargeId);

                //校验部门信息
                List<Root> unitList = rootMapper.getUnitByEmployeeId(root);
                if (CollectionUtils.isEmpty(unitList)) {
                    addErrorMsg(i, "该责任人未分配部门");
                    continue;
                }
                Long chargeDeptId = null;
                if (StringUtils.isNotEmpty(root.getChargeDept())) {
                    if (DataSecurityHelper.isTenantOpen()) {
                        root.setChargeDept(DataSecurityHelper.encrypt(root.getChargeDept()));
                    }
                    for (Root tmp : unitList) {
                        if (root.getChargeDept().equals(tmp.getChargeDept())) {
                            chargeDeptId = tmp.getChargeDeptId();
                        }
                    }
                } else {
                    chargeDeptId = unitList.get(0).getChargeDeptId();
                }
                if (ObjectUtils.isEmpty(chargeDeptId)) {
                    addErrorMsg(i, "责任人未分配至该责任部门");
                }


                //英文简称是否存在
                if (CollectionUtils.isNotEmpty(rootList)) {
                    Root rootExist = rootList.get(0);
                    //判断是否在线
                    if (StandardConstant.Status.ONLINE.equals(rootExist.getReleaseStatus())
                            || StandardConstant.Status.OFFLINE_APPROVING.equals(rootExist.getReleaseStatus())) {
                        String offlineFlag = profileClient.getProfileValueByOptions(tenantId, null, null, WorkFlowConstant.OpenConfig.ROOT_OFFLINE);
                        //下线是否需要审批
                        if(Boolean.parseBoolean(offlineFlag)){
                            addErrorMsg(i,"词根已存在，状态不允许做修改操作，请先下线");
                        }
                    }
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
