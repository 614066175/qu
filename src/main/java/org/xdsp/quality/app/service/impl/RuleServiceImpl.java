package org.xdsp.quality.app.service.impl;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.CommonGroupClient;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.api.dto.RuleLineDTO;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.app.service.RuleService;
import org.xdsp.quality.domain.entity.Rule;
import org.xdsp.quality.domain.entity.RuleGroup;
import org.xdsp.quality.domain.repository.RuleGroupRepository;
import org.xdsp.quality.domain.repository.RuleLineRepository;
import org.xdsp.quality.domain.repository.RuleRepository;
import org.xdsp.quality.infra.constant.CheckConstants;
import org.xdsp.quality.infra.converter.RuleConverter;
import org.xdsp.quality.infra.converter.RuleLineConverter;
import org.xdsp.quality.infra.util.JsonUtils;

import java.util.List;

/**
 * <p>规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Service
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final RuleLineRepository ruleLineRepository;
    private final RuleConverter ruleConverter;
    private final RuleLineConverter ruleLineConverter;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleServiceImpl(RuleRepository ruleRepository,
                           RuleLineRepository ruleLineRepository,
                           RuleConverter ruleConverter,
                           RuleLineConverter ruleLineConverter,
                           RuleGroupRepository ruleGroupRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleLineRepository = ruleLineRepository;
        this.ruleConverter = ruleConverter;
        this.ruleLineConverter = ruleLineConverter;
        this.ruleGroupRepository = ruleGroupRepository;
    }


    @Override
    public RuleDTO detail(Long ruleId, Long tenantId, Long projectId) {
        RuleDTO ruleDTO = ruleRepository.selectDTOByPrimaryKey(ruleId);
        List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.list(ruleId, tenantId, projectId);
        for (RuleLineDTO ruleLineDTO : ruleLineDTOList) {
            ruleLineDTO.setWarningLevelList(JsonUtils.json2WarningLevel(ruleLineDTO.getWarningLevel()));
        }
        ruleDTO.setRuleLineDTOList(ruleLineDTOList);
        return ruleDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(RuleDTO ruleDTO) {
        Long tenantId = ruleDTO.getTenantId();
        ruleRepository.insertDTOSelective(ruleDTO);
        if (ruleDTO.getRuleLineDTOList() != null) {

            for (RuleLineDTO ruleLineDTO : ruleDTO.getRuleLineDTOList()) {
                ruleLineDTO.setRuleId(ruleDTO.getRuleId());
                ruleLineDTO.setTenantId(tenantId);
                //todo 范围重叠判断
                ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));

                if(CheckConstants.NOT_EMPTY.equals(ruleLineDTO.getCheckItem())){
                    ruleLineDTO.setCheckWay(CheckConstants.COMMON);
                    ruleLineDTO.setCountType(CheckConstants.FIXED_VALUE);
                    ruleLineDTO.setCompareWay(CheckConstants.VALUE);
                    List<WarningLevelDTO> warningLevelDTOS = ruleLineDTO.getWarningLevelList();
                    warningLevelDTOS.forEach(tmp->{
                        tmp.setCompareSymbol("NOT_EQUAL");
                        tmp.setExpectedValue("0");
                    });
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(warningLevelDTOS));
                }
                ruleLineRepository.insertDTOSelective(ruleLineDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RuleDTO ruleDTO) {
        Long tenantId = ruleDTO.getTenantId();
        Long projectId = ruleDTO.getProjectId();
        ruleRepository.updateDTOAllColumnWhereTenant(ruleDTO, tenantId);
        if (ruleDTO.getRuleLineDTOList() != null) {
            for (RuleLineDTO ruleLineDTO : ruleDTO.getRuleLineDTOList()) {
                if(CheckConstants.NOT_EMPTY.equals(ruleLineDTO.getCheckItem())){
                    ruleLineDTO.setCheckWay(CheckConstants.COMMON);
                    ruleLineDTO.setCountType(CheckConstants.FIXED_VALUE);
                    ruleLineDTO.setCompareWay(CheckConstants.VALUE);
                    List<WarningLevelDTO> warningLevelDTOS = ruleLineDTO.getWarningLevelList();
                    warningLevelDTOS.forEach(tmp->{
                        tmp.setCompareSymbol("NOT_EQUAL");
                        tmp.setExpectedValue("0");
                    });
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(warningLevelDTOS));
                }
                if (AuditDomain.RecordStatus.update.equals(ruleLineDTO.get_status())) {
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineDTO.setProjectId(projectId);
                    ruleLineRepository.updateDTOAllColumnWhereTenant(ruleLineDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(ruleLineDTO.get_status())) {
                    ruleLineDTO.setRuleId(ruleDTO.getRuleId());
                    ruleLineDTO.setProjectId(projectId);
                    ruleLineDTO.setTenantId(tenantId);
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.insertDTOSelective(ruleLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(ruleLineDTO.get_status())) {
                    ruleLineRepository.deleteByPrimaryKey(ruleLineDTO);
                }

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSite(RuleDTO ruleDTO) {
        Rule rule = ruleConverter.dtoToEntity(ruleDTO);
        ruleRepository.updateByPrimaryKey(rule);
        if (ruleDTO.getRuleLineDTOList() != null) {
            for (RuleLineDTO ruleLineDTO : ruleDTO.getRuleLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(ruleLineDTO.get_status())) {
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.updateByPrimaryKey(ruleLineConverter.dtoToEntity(ruleLineDTO));
                } else if (AuditDomain.RecordStatus.create.equals(ruleLineDTO.get_status())) {
                    ruleLineDTO.setRuleId(rule.getRuleId());
                    ruleLineDTO.setTenantId(0L);
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.insertDTOSelective(ruleLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(ruleLineDTO.get_status())) {
                    ruleLineRepository.deleteByPrimaryKey(ruleLineDTO);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(RuleDTO ruleDTO) {
        ruleLineRepository.deleteByParentId(ruleDTO.getRuleId());
        return ruleRepository.deleteByPrimaryKey(ruleDTO);
    }

    @Override
    public List<RuleDTO> export(RuleDTO dto, ExportParam exportParam) {
        return ruleRepository.listAll(dto);
    }

    @Override
    public Page<RuleDTO> pageRules(PageRequest pageRequest, RuleDTO ruleDTO) {
        //分组查询时同时查询当前分组和当前分组子分组的数据标准
        Long groupId = ruleDTO.getGroupId();
        if (ObjectUtils.isNotEmpty(groupId)) {
//            List<RuleGroupDTO> ruleGroupDTOList = new ArrayList<>();
//            if (groupId == 0) {
//                ruleDTO.setGroupId(null);
//            }
            //查询子分组
//            findChildGroups(groupId,ruleGroupDTOList);
//            //添加当前分组
//            ruleGroupDTOList.add(RuleGroupDTO.builder().groupId(groupId).build());
//            Long[] groupIds = ruleGroupDTOList.stream().map(RuleGroupDTO::getGroupId).toArray(Long[]::new);
//            ruleDTO.setGroupArrays(groupIds);
            CommonGroupRepository commonGroupRepository = ApplicationContextHelper.getContext().getBean(CommonGroupRepository.class);
            CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(groupId);
            CommonGroupClient commonGroupClient = ApplicationContextHelper.getContext().getBean(CommonGroupClient.class);
            List<CommonGroup> subGroup = commonGroupClient.getSubGroup(commonGroup);
            subGroup.add(commonGroup);
            ruleDTO.setGroupArrays(subGroup.stream().map(CommonGroup::getGroupId).toArray(Long[]::new));
        }
        return ruleRepository.listTenant(pageRequest, ruleDTO);
    }

    private void findChildGroups(Long groupId, List<RuleGroupDTO> ruleGroupDTOList) {
        List<RuleGroupDTO> standardGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_PARENT_GROUP_ID, groupId))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            ruleGroupDTOList.addAll(standardGroupDTOList);
            standardGroupDTOList.forEach(standardGroupDTO -> findChildGroups(standardGroupDTO.getGroupId(), ruleGroupDTOList));
        }
    }
}
