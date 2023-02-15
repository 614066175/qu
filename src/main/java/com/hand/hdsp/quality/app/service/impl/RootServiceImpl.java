package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.AssigneeUserDTO;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.export.RootExport;
import com.hand.hdsp.quality.infra.export.dto.RootExportDTO;
import com.hand.hdsp.quality.infra.mapper.StandardApprovalMapper;
import com.hand.hdsp.quality.infra.util.AnsjUtil;
import com.hand.hdsp.quality.workflow.adapter.RootOfflineWorkflowAdapter;
import com.hand.hdsp.quality.workflow.adapter.RootOnlineWorkflowAdapter;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.boot.platform.plugin.hr.entity.Employee;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.dto.ProcessInstanceDTO;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.ROOT;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

/**
 * 词根应用服务默认实现
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@Slf4j
@Service
public class RootServiceImpl implements RootService {

    private final RootRepository rootRepository;
    private final RootVersionRepository rootVersionRepository;
    private final RootLineRepository rootLineRepository;
    private final CommonGroupRepository commonGroupRepository;
    private final StandardApprovalRepository standardApprovalRepository;
    private final StandardApprovalMapper standardApprovalMapper;
    private final ProfileClient profileClient;
    private final WorkflowClient workflowClient;
    private final RootOnlineWorkflowAdapter rootOnlineWorkflowAdapter;
    private final RootOfflineWorkflowAdapter rootOfflineWorkflowAdapter;

    private static final String PATTERN = "^[A-Za-z][A-Za-z0-9]{0,63}$";

    private static final String NO_MATCH = "{未匹配}";

    @Value("${hdsp.root_dic_parent:library}")
    public String dicParent;

    public static final String DIC_NAME_FORMAT = "rootLibrary_%d_%d.dic";

    private static final Long DEFAULT_VERSION = 1L;

    private final AnsjUtil ansjUtil;

    private final RootDicRepository rootDicRepository;

    public RootServiceImpl(RootRepository rootRepository, RootVersionRepository rootVersionRepository, RootLineRepository rootLineRepository, CommonGroupRepository commonGroupRepository, StandardApprovalRepository standardApprovalRepository, StandardApprovalMapper standardApprovalMapper, ProfileClient profileClient, WorkflowClient workflowClient, RootOnlineWorkflowAdapter rootOnlineWorkflowAdapter, RootOfflineWorkflowAdapter rootOfflineWorkflowAdapter, AnsjUtil ansjUtil, RootDicRepository rootDicRepository) {
        this.rootRepository = rootRepository;
        this.rootVersionRepository = rootVersionRepository;
        this.rootLineRepository = rootLineRepository;
        this.commonGroupRepository = commonGroupRepository;
        this.standardApprovalRepository = standardApprovalRepository;
        this.standardApprovalMapper = standardApprovalMapper;
        this.profileClient = profileClient;
        this.workflowClient = workflowClient;
        this.rootOnlineWorkflowAdapter = rootOnlineWorkflowAdapter;
        this.rootOfflineWorkflowAdapter = rootOfflineWorkflowAdapter;
        this.ansjUtil = ansjUtil;
        this.rootDicRepository = rootDicRepository;
    }

    @Override
    public Page<Root> list(PageRequest pageRequest, Root root) {
        Long groupId = root.getGroupId();
        if (ObjectUtils.isNotEmpty(groupId)) {
            CommonGroupClient commonGroupClient = ApplicationContextHelper.getContext().getBean(CommonGroupClient.class);
            CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(root.getGroupId());
            List<CommonGroup> subGroup = commonGroupClient.getSubGroup(commonGroup);
            subGroup.add(commonGroup);
            Long[] groupIds = subGroup.stream().map(CommonGroup::getGroupId).toArray(Long[]::new);
            root.setGroupArrays(groupIds);
            root.setGroupId(null);
        }
        return PageHelper.doPageAndSort(pageRequest, () -> rootRepository.list(root));
    }

    @Override
    public Root detail(Long id) {
        List<Root> list = rootRepository.list(Root.builder().id(id).build());
        Root root = null;
        if (CollectionUtils.isNotEmpty(list)) {
            root = list.get(0);
        }
        return root;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Root root) {
        if (!Pattern.matches(PATTERN, root.getRootEnShort())) {
            throw new CommonException(ErrorCode.ROOT_EN_SHORT_ERROR);
        }

        List<Root> rootList = rootRepository.selectByCondition(Condition.builder(Root.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Root.FIELD_ROOT_EN_SHORT, root.getRootEnShort())
                        .andEqualTo(Root.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(Root.FIELD_TENANT_ID, root.getTenantId())
                ).build());
        if (CollectionUtils.isNotEmpty(rootList)) {
            throw new CommonException(ErrorCode.ROOT_EN_SHORT_EXIST);
        }

        //词根中文校验
        String[] rootName = root.getRootName().split(StandardConstant.RootName.SEPARATOR);

        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom().andIn(RootLine.FIELD_ROOT_NAME, Arrays.asList(rootName))
                        .andEqualTo(RootLine.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID, root.getTenantId())
                ).build());

        if (CollectionUtils.isNotEmpty(rootLines)) {
            List<String> str = rootLines.stream().map(RootLine::getRootName).collect(Collectors.toList());
            String rootNameStr = StringUtils.join(str, StandardConstant.RootName.SEPARATOR);
            throw new CommonException(ErrorCode.ROOT_NAME_EXIST, rootNameStr);
        }

        root.setReleaseStatus(StandardConstant.Status.CREATE);
        rootRepository.insertSelective(root);
        rootLines = new ArrayList<>();
        RootLine rootLine;
        for (int i = 0; i < rootName.length; i++) {
            rootLine = RootLine.builder()
                    .rootId(root.getId())
                    .rootName(rootName[i])
                    .projectId(root.getProjectId())
                    .tenantId(root.getTenantId())
                    .build();
            rootLines.add(rootLine);
        }
        rootLineRepository.batchInsert(rootLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Root root) {
        rootRepository.updateByPrimaryKey(root);

        //删除行表数据，重新插入
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootLine.FIELD_ROOT_ID, root.getId())
                        .andEqualTo(RootLine.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID, root.getTenantId())
                ).build());
        rootLineRepository.batchDeleteByPrimaryKey(rootLines);

        String[] rootName = root.getRootName().split(StandardConstant.RootName.SEPARATOR);
        rootLines = new ArrayList<>();
        for (int i = 0; i < rootName.length; i++) {
            RootLine rootLine = RootLine.builder()
                    .rootId(root.getId())
                    .rootName(rootName[i])
                    .projectId(root.getProjectId())
                    .tenantId(root.getTenantId())
                    .build();
            rootLines.add(rootLine);
        }
        rootLineRepository.batchInsert(rootLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Root root) {
        if (ONLINE.equals(root.getReleaseStatus())
                || OFFLINE_APPROVING.equals(root.getReleaseStatus())
                || ONLINE_APPROVING.equals(root.getReleaseStatus())) {
            throw new CommonException(ErrorCode.ROOT_NOT_DELETE);
        }
        rootRepository.deleteByPrimaryKey(root);

        //删除行表数据
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootLine.FIELD_ROOT_ID, root.getId())
                        .andEqualTo(RootLine.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID, root.getTenantId())
                ).build());
        rootLineRepository.batchDeleteByPrimaryKey(rootLines);

        //删除版本表数据
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootVersion.FIELD_ROOT_ID, root.getId())
                        .andEqualTo(RootVersion.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(RootVersion.FIELD_TENANT_ID, root.getTenantId())
                ).build());
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            rootVersionRepository.batchDeleteByPrimaryKey(rootVersions);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Root> rootList) {
        rootList.forEach(root -> {
            if (ONLINE.equals(root.getReleaseStatus())
                    || OFFLINE_APPROVING.equals(root.getReleaseStatus())
                    || ONLINE_APPROVING.equals(root.getReleaseStatus())) {
                throw new CommonException(ErrorCode.ROOT_NOT_DELETE);
            }
        });
        rootRepository.batchDeleteByPrimaryKey(rootList);

        //删除行表数据
        List<Long> ids = rootList.stream().map(Root::getId).collect(Collectors.toList());
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andIn(RootLine.FIELD_ROOT_ID, ids)
                ).build());
        rootLineRepository.batchDeleteByPrimaryKey(rootLines);

        //删除版本表数据
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andIn(RootVersion.FIELD_ROOT_ID, ids)
                ).build());
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            rootVersionRepository.batchDeleteByPrimaryKey(rootVersions);
        }
    }

    @Override
    public List<RootExportDTO> export(Root root, ExportParam exportParam) {
        RootExport rootExport = ApplicationContextHelper.getContext().getBean(RootExport.class);
        return rootExport.export(root);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(Root root) {
        String onlineFlag = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(), null, null, WorkFlowConstant.OpenConfig.ROOT_ONLINE);
        String offlineFlag = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(), null, null, WorkFlowConstant.OpenConfig.ROOT_OFFLINE);
        if ("true".equals(onlineFlag) && ONLINE.equals(root.getReleaseStatus())) {
            //启动工作流，启动工作流需要花费一定时间,有异常回滚
            rootOnlineWorkflowAdapter.startWorkflow(root);
            root.setReleaseStatus(ONLINE_APPROVING);
        }
        if ("true".equals(offlineFlag) && OFFLINE.equals(root.getReleaseStatus())) {
            //启动工作流，启动工作流需要花费一定时间,有异常回滚
            rootOfflineWorkflowAdapter.startWorkflow(root);
            root.setReleaseStatus(OFFLINE_APPROVING);
        }

        if (("false".equals(offlineFlag) && ONLINE.equals(root.getReleaseStatus())) ||
                ("false".equals(onlineFlag) && OFFLINE.equals(root.getReleaseStatus()))) {
            Root rootTmp = rootRepository.selectByPrimaryKey(root.getId());
            if (Objects.isNull(rootTmp)) {
                throw new CommonException(ErrorCode.ROOT_NOT_EXIST);
            }
            root.setObjectVersionNumber(rootTmp.getObjectVersionNumber());
            if (ONLINE.equals(root.getReleaseStatus())) {
                //存版本表
                doVersion(root);
                root.setReleaseBy(DetailsHelper.getUserDetails().getUserId());
                root.setReleaseDate(new Date());
                rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS, Root.FIELD_RELEASE_BY, Root.FIELD_RELEASE_DATE);
                return;
            }
        }
        //修改发布状态
        rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
    }

    @Override
    public void onlineWorkflowCallback(Long rootId, String nodeApproveResult) {
        Root root = rootRepository.selectByPrimaryKey(rootId);
        if (root != null) {
            //工作流适配器回调
            root = (Root) rootOnlineWorkflowAdapter.callBack(root, nodeApproveResult);

            if (ONLINE.equals(root.getReleaseStatus())) {
                List<StandardApprovalDTO> standardApprovalDTOS = standardApprovalRepository.selectDTOByCondition(Condition.builder(StandardApproval.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardApproval.FIELD_TENANT_ID, root.getTenantId())
                                .andEqualTo(StandardApproval.FIELD_STANDARD_ID, rootId)
                                .andEqualTo(StandardApproval.FIELD_STANDARD_TYPE, ROOT)
                                .andEqualTo(StandardApproval.FIELD_APPLY_TYPE, ONLINE))
                        .orderByDesc(StandardApproval.FIELD_APPROVAL_ID)
                        .build());
                if (CollectionUtils.isNotEmpty(standardApprovalDTOS)) {
                    StandardApprovalDTO standardApprovalDTO = standardApprovalDTOS.get(0);
                    root.setReleaseBy(standardApprovalDTO.getCreatedBy());
                    root.setReleaseDate(new Date());
                }
                rootRepository.updateOptional(root, Root.FIELD_RELEASE_BY, Root.FIELD_RELEASE_DATE);

                doVersion(root);
                //上线后词库追加词根对应的中文
                List<RootLine> rootLines = rootLineRepository.select(RootLine.builder().rootId(rootId).build());
                if (CollectionUtils.isNotEmpty(rootLines)) {
                    List<String> addWords = rootLines.stream()
                            .filter(rootLine -> Strings.isNotBlank(rootLine.getRootName()))
                            .map(RootLine::getRootName)
                            .distinct()
                            .collect(Collectors.toList());
                    ansjUtil.addWord(root.getTenantId(), root.getProjectId(), addWords);
                }
            } else {
                //上线审批拒绝
                rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
            }
        }
    }

    @Override
    public void offlineWorkflowCallback(Long rootId, String nodeApproveResult) {
        Root root = rootRepository.selectByPrimaryKey(rootId);
        if (root != null) {
            //工作流适配器回调
            root = (Root) rootOfflineWorkflowAdapter.callBack(root, nodeApproveResult);
            rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
            if (OFFLINE.equals(root.getReleaseStatus())) {
                //下线词库中进行移除,基于当前在线和下线中的词根重新生成文件
                ansjUtil.rebuildDic(root.getTenantId(), root.getProjectId());
            }
        }
    }

    @Override
    public List<AssigneeUserDTO> findCharger(Long chargeId) {
        AssigneeUserDTO assigneeUserDTO = rootRepository.getAssigneeUser(chargeId);
        return Collections.singletonList(assigneeUserDTO);
    }

    @Override
    public StandardApprovalDTO rootApplyInfo(Long tenantId, Long approvalId) {
        StandardApprovalDTO standardApprovalDTO = standardApprovalRepository.selectDTOByPrimaryKey(approvalId);
        if (!(Objects.nonNull(standardApprovalDTO) && Objects.nonNull(standardApprovalDTO.getApprovalId()))) {
            throw new CommonException(ErrorCode.NO_APPROVAL_INSTANCE);
        }
        ProcessInstanceDTO.ProcessInstanceViewDTO instanceView = workflowClient.getInstanceDetailByInstanceId(tenantId, standardApprovalDTO.getInstanceId());
        List<ProcessInstanceDTO.ProcessInstanceCurrentNodeDTO> nodeDTOList = instanceView.getNodeDTOList();
        StandardApprovalDTO approvalDTO = StandardApprovalDTO
                .builder()
                .flowName(instanceView.getFlowName())
                .businessKey(instanceView.getBusinessKey())
                .applyDate(instanceView.getStartDate())
                .build();
        Employee employee = EmployeeHelper.getEmployee(standardApprovalDTO.getApplicantId(), standardApprovalDTO.getTenantId());
        approvalDTO.setEmployeeTel(employee.getMobile());
        approvalDTO.setEmployeeEmail(employee.getEmail());
        approvalDTO.setEmployeeName(employee.getName());
        List<String> employeeUnitList = standardApprovalMapper.getEmployeeUnit(employee);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(employeeUnitList)) {
            String unitName = employeeUnitList.get(0);
            if (DataSecurityHelper.isTenantOpen()) {
                unitName = DataSecurityHelper.decrypt(unitName);
            }
            approvalDTO.setApplyUnitName(unitName);
        }
        if (CollectionUtils.isNotEmpty(nodeDTOList)) {
            approvalDTO.setCurrentNode(nodeDTOList.get(0).getCurrentNode());
        }
        long lastVersion = 1L;
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootVersion.FIELD_ROOT_ID, standardApprovalDTO.getStandardId()))
                .orderByDesc(RootVersion.FIELD_VERSION_NUMBER)
                .build());
        //不为空则取最新版本
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            lastVersion = rootVersions.get(0).getVersionNumber() + 1;
        }
        approvalDTO.setSubmitVersion(String.format("v%s.0", lastVersion));
        return approvalDTO;
    }

    @Override
    public Root rootInfo(Long approvalId) {
        StandardApprovalDTO standardApprovalDTO = standardApprovalRepository.selectDTOByPrimaryKey(approvalId);
        if (Objects.isNull(standardApprovalDTO)) {
            throw new CommonException(ErrorCode.NO_APPROVAL_INSTANCE);
        }
        return this.detail(standardApprovalDTO.getStandardId());
    }

    @Override
    public String analyzerWord(Long tenantId, Long projectId, String word) {
        //判断词库是否存在
        RootDic rootDic = rootDicRepository.selectOne(RootDic.builder().tenantId(tenantId).projectId(projectId).build());
        if (rootDic == null) {
            return StringUtils.EMPTY;
        }
        //获取dic词库
        File rootLibrary = ansjUtil.getDic(tenantId, projectId);
        List<Term> terms;
        if (!rootLibrary.exists()) {
            return StringUtils.EMPTY;
        }
        try {
            Forest forest = Library.makeForest(rootLibrary.getAbsolutePath());
            Result result = DicAnalysis.parse(word, forest);
            terms = result.getTerms();
            if (CollectionUtils.isEmpty(terms)) {
                throw new CommonException("输入内容不合法,无法分词分析");
            }
        } catch (Exception e) {
            throw new CommonException("词根分析失败", e);
        }
        List<String> roots = new ArrayList<>();
        terms.forEach(term -> {
            //获取每个term对应的词根
            String name = term.getName();
            RootLine rootLine = rootLineRepository.selectOne(RootLine.builder().rootName(name)
                    .tenantId(tenantId).projectId(projectId).build());
            if (rootLine == null) {
                roots.add(NO_MATCH);
            } else {
                Root root = rootRepository.selectByPrimaryKey(rootLine.getRootId());
                //判断对应词根是否发布
                if (root == null || !Arrays.asList(ONLINE, OFFLINE_APPROVING).contains(root.getReleaseStatus())) {
                    roots.add(NO_MATCH);
                } else {
                    roots.add(root.getRootEnShort());
                }
            }
        });
        return StringUtils.join(roots, "_");
    }

    @Override
    public List<String> rootTranslate(Long tenantId, Long projectId, String word) {
        List<List<String>> results = new ArrayList<>();
        if (StringUtils.isBlank(word)) {
            return new ArrayList<>();
        }
        String[] words = word.split("_");
        if (words.length <= 0) {
            return new ArrayList<>();
        }
        for (String rootEnShort : words) {
            //根据rootEn找到对应词根对应的中文
            List<Root> roots = rootRepository.selectByCondition(Condition.builder(Root.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(Root.FIELD_ROOT_EN_SHORT, rootEnShort)
                            .andEqualTo(Root.FIELD_TENANT_ID, tenantId)
                            .andEqualTo(Root.FIELD_PROJECT_ID, projectId)
                            .andIn(Root.FIELD_RELEASE_STATUS, Arrays.asList(ONLINE, OFFLINE_APPROVING)))
                    .build());
            if (CollectionUtils.isEmpty(roots)) {
                results.add(Collections.singletonList(NO_MATCH));
            } else {
                Root root = roots.get(0);
                List<RootLine> rootLines = rootLineRepository.select(RootLine.builder().rootId(root.getId()).build());
                if (CollectionUtils.isEmpty(rootLines)) {
                    results.add(Collections.singletonList(NO_MATCH));
                } else {
                    List<String> rootNames = rootLines.stream().map(RootLine::getRootName).distinct().collect(Collectors.toList());
                    results.add(rootNames);
                }
            }
        }
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }
        List<String> finalResult = results.get(0);
        for (int i = 1; i < results.size(); i++) {
            int finalI = i;
            finalResult = finalResult.stream()
                    .flatMap(str -> results.get(finalI).stream().map(str::concat))
                    .collect(Collectors.toList());
        }
        return finalResult;
    }

    /**
     * 版本记录
     *
     * @param root
     */
    @Override
    public void doVersion(Root root) {
        Long versionNum = DEFAULT_VERSION;
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootVersion.FIELD_ROOT_ID, root.getId()))
                .orderByDesc(RootVersion.FIELD_VERSION_NUMBER)
                .build());
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            versionNum = rootVersions.get(0).getVersionNumber() + 1;
        }
        root = rootRepository.list(Root.builder().id(root.getId()).build()).get(0);
        RootVersion rootVersion = new RootVersion();
        BeanUtils.copyProperties(root, rootVersion);
        rootVersion.setId(null);
        rootVersion.setRootId(root.getId());
        rootVersion.setVersionNumber(versionNum);
        rootVersionRepository.insert(rootVersion);
    }

    private void findChildGroups(Long groupId, List<CommonGroup> commonGroupList) {
        List<CommonGroup> childGroupList = commonGroupRepository.selectByCondition(
                Condition.builder(CommonGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(CommonGroup.FIELD_PARENT_GROUP_ID, groupId))
                        .build());
        if (CollectionUtils.isNotEmpty(childGroupList)) {
            commonGroupList.addAll(childGroupList);
            childGroupList.forEach(commonGroup -> findChildGroups(commonGroup.getGroupId(), commonGroupList));
        }
    }
}
