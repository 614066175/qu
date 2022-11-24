package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.RootDTO;
import com.hand.hdsp.quality.api.dto.RootLineDTO;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.domain.repository.RootLineRepository;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.RootMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

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

    public RootValidator(ObjectMapper objectMapper, RootMapper rootMapper, RootRepository rootRepository, RootLineRepository rootLineRepository) {
        this.objectMapper = objectMapper;
        this.rootMapper = rootMapper;
        this.rootRepository = rootRepository;
        this.rootLineRepository = rootLineRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        Set<String> rootNameSet = new HashSet<>();
        for(int i=0;i<data.size();i++){
            try {
                Root root = objectMapper.readValue(data.get(i), Root.class);
                root.setTenantId(tenantId);
                root.setProjectId(projectId);
                
                List<Root> rootList = rootRepository.selectByCondition(Condition.builder(Root.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(Root.FIELD_ROOT_EN_SHORT,root.getRootEnShort())
                                .andEqualTo(Root.FIELD_PROJECT_ID,root.getProjectId())
                                .andEqualTo(Root.FIELD_TENANT_ID,root.getTenantId())
                        ).build());
                if(CollectionUtils.isNotEmpty(rootList)){
                    addErrorMsg(i,ErrorCode.ROOT_EN_SHORT_EXIST);
                }

                //词根中文校验
                String[] rootName =  root.getRootName().split(StandardConstant.RootName.SEPARATOR);
                List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                        .andWhere(Sqls.custom().andIn(RootLine.FIELD_ROOT_NAME, Arrays.asList(rootName))
                                .andEqualTo(RootLine.FIELD_PROJECT_ID,root.getProjectId())
                                .andEqualTo(RootLine.FIELD_TENANT_ID,root.getTenantId())
                        ).build());
                StringBuffer rootNameStr = new StringBuffer();
                if(CollectionUtils.isNotEmpty(rootLines)){
                    for (RootLine tmp: rootLines){
                        rootNameStr.append(tmp.getRootName()).append(" ");
                    }
                    addErrorMsg(i,String.format("词根中文名称%s数据库中已存在!",rootNameStr));
                }

                //校验表中词根中文重复
                rootNameStr = new StringBuffer();
                for(int j=0;j<rootName.length;j++){
                    if(!rootNameSet.add(rootName[j])){
                        rootNameStr.append(rootName[j]).append(" ");
                    }
                }
                if(StringUtils.isNotEmpty(rootNameStr)){
                    addErrorMsg(i,String.format("词根中文名称%s表中已存在!",rootNameStr));
                }

                //校验的责任人名称为员工姓名
                if(DataSecurityHelper.isTenantOpen()){
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
                if(CollectionUtils.isEmpty(unitList)){
                    addErrorMsg(i,"该责任人未分配部门");
                    continue;
                }
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
                    addErrorMsg(i,"责任人未分配至该责任部门");
                    continue;
                }

                //当sheet页”数据标准“中的“分组名称”字段在本次导入表格和系统中不存在时则不能导入，并提示”${分组}分组不存在“，并在对应单元格高亮警示
                String groupCode = root.getGroupCode();
                if (StringUtils.isEmpty(groupCode)) {
                    addErrorMsg(i, String.format("表格中不存在分组%s", groupCode));
                    continue;
                }
            }catch (Exception e){
                log.info(e.getMessage());
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
