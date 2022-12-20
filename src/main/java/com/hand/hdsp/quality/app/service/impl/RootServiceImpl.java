package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.mapper.StandardApprovalMapper;
import com.hand.hdsp.quality.infra.util.AnsjUtil;
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
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.ProcessInstanceDTO;
import org.hzero.boot.workflow.dto.RunInstance;
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
    private final StandardGroupRepository standardGroupRepository;
    private final StandardApprovalService standardApprovalService;
    private final StandardApprovalRepository standardApprovalRepository;
    private final StandardApprovalMapper standardApprovalMapper;
    private final ProfileClient profileClient;
    private final WorkflowClient workflowClient;

    private static final String PATTERN = "^[A-Za-z][A-Za-z0-9]{0,63}$";

    private static final String NO_MATCH = "{未匹配}";

    @Value("${hdsp.root_dic_parent:library}")
    public String dicParent;

    public static final String DIC_NAME_FORMAT = "rootLibrary_%d_%d.dic";

    private static final Long DEFAULT_VERSION = 1L;

    private final AnsjUtil ansjUtil;

    private final RootDicRepository rootDicRepository;

    public RootServiceImpl(RootRepository rootRepository, RootVersionRepository rootVersionRepository, RootLineRepository rootLineRepository, StandardGroupRepository standardGroupRepository, StandardApprovalService standardApprovalService, StandardApprovalRepository standardApprovalRepository, StandardApprovalMapper standardApprovalMapper, ProfileClient profileClient, WorkflowClient workflowClient, AnsjUtil ansjUtil, RootDicRepository rootDicRepository) {
        this.rootRepository = rootRepository;
        this.rootVersionRepository = rootVersionRepository;
        this.rootLineRepository = rootLineRepository;
        this.standardGroupRepository = standardGroupRepository;
        this.standardApprovalService = standardApprovalService;
        this.standardApprovalRepository = standardApprovalRepository;
        this.standardApprovalMapper = standardApprovalMapper;
        this.profileClient = profileClient;
        this.workflowClient = workflowClient;
        this.ansjUtil = ansjUtil;
        this.rootDicRepository = rootDicRepository;
    }

    @Override
    public Page<Root> list(PageRequest pageRequest, Root root) {
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
            StringBuffer rootNameStr = new StringBuffer();
            for (RootLine rootLine : rootLines) {
                rootNameStr.append(rootLine.getRootName()).append(" ");
            }
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
        rootRepository.updateByPrimaryKeySelective(root);

        //删除行表数据，重新插入
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootLine.FIELD_ROOT_ID, root.getId())
                        .andEqualTo(RootLine.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID, root.getTenantId())
                ).build());
        rootLineRepository.batchDelete(rootLines);

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
        rootLineRepository.batchDelete(rootLines);

        //删除版本表数据
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootVersion.FIELD_ROOT_ID, root.getId())
                        .andEqualTo(RootVersion.FIELD_PROJECT_ID, root.getProjectId())
                        .andEqualTo(RootVersion.FIELD_TENANT_ID, root.getTenantId())
                ).build());
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            rootVersionRepository.batchDelete(rootVersions);
        }
    }

    @Override
    public void batchDelete(List<Root> rootList) {
        rootList.forEach(root->{
            if (ONLINE.equals(root.getReleaseStatus())
                    || OFFLINE_APPROVING.equals(root.getReleaseStatus())
                    || ONLINE_APPROVING.equals(root.getReleaseStatus())) {
                throw new CommonException(ErrorCode.ROOT_NOT_DELETE);
            }
        });
        rootRepository.batchDelete(rootList);

        //删除行表数据
        List<Long> ids = rootList.stream().map(Root::getId).collect(Collectors.toList());
        List<RootLine> rootLines= rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andIn(RootLine.FIELD_ROOT_ID,ids)
                ).build());
        rootLineRepository.batchDelete(rootLines);

        //删除版本表数据
        List<RootVersion> rootVersions= rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andIn(RootVersion.FIELD_ROOT_ID,ids)
                ).build());
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            rootVersionRepository.batchDelete(rootVersions);
        }
    }

    @Override
    public List<RootGroupDTO> export(Root root, ExportParam exportParam) {
        List<RootGroupDTO> rootGroupDTOS = new ArrayList<>();
        int level = 1;
        if (ObjectUtils.isNotEmpty(root.getGroupId())) {
            //分组条件导出
            StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                    .andEqualTo(StandardGroup.FIELD_TENANT_ID, root.getTenantId())
                    .andEqualTo(StandardGroup.FIELD_PROJECT_ID, root.getProjectId())
                    .andEqualTo(StandardGroup.FIELD_GROUP_ID, root.getGroupId())
            ).build()).get(0);
            //获取设置当前分组的父分组编码
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(groupDTO.getParentGroupId());
                groupDTO.setParentGroupCode(parentGroupDTO.getGroupCode());
            }
            //查询当前分组数据
            List<Root> roots = rootRepository.list(Root.builder().groupId(groupDTO.getGroupId()).build());
            RootGroupDTO dto = new RootGroupDTO();
            BeanUtils.copyProperties(groupDTO, dto);
            dto.setRoots(roots);
            dto.setGroupLevel(level);
            rootGroupDTOS.add(dto);
            //添加查询父分组 并排序
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                findParentGroups(groupDTO.getParentGroupId(), rootGroupDTOS, level);
            }
            findChildGroups(dto, rootGroupDTOS, level);
            return rootGroupDTOS.stream().sorted(Comparator.comparing(RootGroupDTO::getGroupLevel)).collect(Collectors.toList());
        } else {
            //全部分组条件导出
            //添加查询所有父分组 并排序导出保证导入准确性
            List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_TENANT_ID, root.getTenantId())
                            .andEqualTo(StandardGroup.FIELD_PROJECT_ID, root.getProjectId())
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, ROOT)
                            .andIsNull(StandardGroup.FIELD_PARENT_GROUP_ID))
                    .build());
            standardGroupDTOList.forEach(standardGroupDTO -> {
                //从所有的根目录 向下查询
                RootGroupDTO dto = new RootGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, dto);
                //查询当前分组数据
                List<Root> roots = rootRepository.list(Root.builder().groupId(standardGroupDTO.getGroupId()).build());
                dto.setRoots(roots);
                dto.setGroupLevel(level);
                rootGroupDTOS.add(dto);
                findChildGroups(dto, rootGroupDTOS, level);
            });
            return rootGroupDTOS.stream().sorted(Comparator.comparing(RootGroupDTO::getGroupLevel)).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(Root root) {
        String onlineFlag = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(),null,null,WorkFlowConstant.ROOT_ONLINE);
        String offlineFlag = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(),null,null,WorkFlowConstant.ROOT_OFFLINE);
        if("true".equals(onlineFlag) && ONLINE.equals(root.getReleaseStatus())){
            //先修改状态再启动工作流，启动工作流需要花费一定时间,有异常回滚
            this.workflowing(root.getId(), ONLINE_APPROVING);
            this.startWorkFlow(WorkFlowConstant.Root.ONLINE_WORKFLOW_KEY, root, ONLINE);
        }
        if("true".equals(offlineFlag) && OFFLINE.equals(root.getReleaseStatus())){
            //先修改状态再启动工作流，启动工作流需要花费一定时间,有异常回滚
            this.workflowing(root.getId(), OFFLINE_APPROVING);
            this.startWorkFlow(WorkFlowConstant.Root.OFFLINE_WORKFLOW_KEY, root, OFFLINE);
        }

        if("false".equals(offlineFlag)&&"false".equals(onlineFlag)){
            Root rootTmp = rootRepository.selectByPrimaryKey(root.getId());
            if (Objects.isNull(rootTmp)) {
                throw new CommonException(ErrorCode.ROOT_NOT_EXIST);
            }
            root.setObjectVersionNumber(rootTmp.getObjectVersionNumber());
            rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
            if(ONLINE.equals(root.getReleaseStatus())){
                //存版本表
                doVersion(root);
            }
        }
    }

    @Override
    public void onlineWorkflowCallback(Long rootId,String nodeApproveResult) {
        if(WorkflowConstant.ApproveAction.APPROVED.equals(nodeApproveResult)){
            workflowing(rootId, ONLINE);
        }else{
            workflowing(rootId, OFFLINE);
        }

    }

    @Override
    public void offlineWorkflowCallback(Long rootId, String nodeApproveResult) {
        if(WorkflowConstant.ApproveAction.APPROVED.equals(nodeApproveResult)){
            workflowing(rootId, OFFLINE);
        }else{
            workflowing(rootId, ONLINE);
        }
    }

    @Override
    public List<AssigneeUserDTO> findCharger(Long rootId) {
        Root root = rootRepository.selectByPrimaryKey(rootId);
        if (root != null) {
            AssigneeUserDTO assigneeUserDTO = rootRepository.getAssigneeUser(root.getChargeId());
            return Collections.singletonList(assigneeUserDTO);
        } else {
            throw new CommonException(ErrorCode.NOT_FIND_VALUE);
        }
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
        UserDTO userInfo = standardApprovalMapper.getUserInfo(standardApprovalDTO.getApplicantId());
        approvalDTO.setEmployeeTel(userInfo.getPhone());
        approvalDTO.setEmployeeEmail(userInfo.getEmail());
        approvalDTO.setEmployeeName(userInfo.getRealName());
        Employee employee = EmployeeHelper.getEmployee(standardApprovalDTO.getApplicantId(), standardApprovalDTO.getTenantId());
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
            RootLine rootLine = rootLineRepository.selectOne(RootLine.builder().rootName(name).tenantId(tenantId).projectId(projectId).build());
            if (rootLine == null) {
                roots.add(NO_MATCH);
            } else {
                Root root = rootRepository.selectByPrimaryKey(rootLine.getRootId());
                roots.add(root.getRootEnShort());
            }
        });
        return StringUtils.join(roots, "_");
    }

    @Override
    public Map<String, List<String>> rootTranslate(Long tenantId, Long projectId, String word) {
        Map<String, List<String>> result = new HashMap<>();
        if (StringUtils.isBlank(word)) {
            return new HashMap<>();
        }
        String[] words = word.split("_");
        if (words.length <= 0) {
            return new HashMap<>();
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
                result.put(rootEnShort, Arrays.asList(NO_MATCH));
            } else {
                Root root = roots.get(0);
                List<RootLine> rootLines = rootLineRepository.select(RootLine.builder().rootId(root.getId()).build());
                if (CollectionUtils.isEmpty(rootLines)) {
                    result.put(rootEnShort, Arrays.asList(NO_MATCH));
                } else {
                    List<String> rootNames = rootLines.stream().map(RootLine::getRootName).distinct().collect(Collectors.toList());
                    result.put(rootEnShort, rootNames);
                }
            }
        }
        return result;
    }

    private void findParentGroups(Long groupId, List<RootGroupDTO> rootGroupDTOs, int level) {
        RootGroupDTO rootGroupDTO = new RootGroupDTO();
        StandardGroupDTO standardGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(groupId);
        level--;

        if (ObjectUtils.isNotEmpty(standardGroupDTO.getParentGroupId())) {
            StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
            standardGroupDTO.setParentGroupCode(parentGroupDTO.getGroupCode());
            findParentGroups(standardGroupDTO.getParentGroupId(), rootGroupDTOs, level);
        }
        BeanUtils.copyProperties(standardGroupDTO, rootGroupDTO);
        rootGroupDTO.setGroupLevel(level);
        rootGroupDTOs.add(rootGroupDTO);
    }

    private void findChildGroups(RootGroupDTO parentRootGroupDTO, List<RootGroupDTO> rootGroupDTOs, int level) {
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, parentRootGroupDTO.getGroupId()))
                .build());
        level++;
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(standardGroupDTO -> {
                RootGroupDTO rootGroupDTO = new RootGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, rootGroupDTO);
                rootGroupDTO.setGroupLevel(finalLevel);
                rootGroupDTO.setParentGroupCode(parentRootGroupDTO.getGroupCode());
                List<Root> roots = rootRepository.list(Root.builder().groupId(rootGroupDTO.getGroupId()).build());
                rootGroupDTO.setRoots(roots);
                rootGroupDTOs.add(rootGroupDTO);
                findChildGroups(rootGroupDTO, rootGroupDTOs, finalLevel);
            });
        }
    }

    /**
     * 修改词根状态，供审批中，审批结束任务状态变更
     *
     * @param id
     * @param status
     */
    private void workflowing(Long id, String status) {
        Root root = rootRepository.selectByPrimaryKey(id);
        if (root != null) {
            root.setReleaseStatus(status);
            rootRepository.updateOptional(root, Root.FIELD_RELEASE_STATUS);
            if (ONLINE.equals(status)) {
                doVersion(root);
                //上线后词库追加词根对应的中文
                List<RootLine> rootLines = rootLineRepository.select(RootLine.builder().rootId(id).build());
                if (CollectionUtils.isNotEmpty(rootLines)) {
                    List<String> addWords = rootLines.stream()
                            .filter(rootLine -> Strings.isNotBlank(rootLine.getRootName()))
                            .map(RootLine::getRootName)
                            .distinct()
                            .collect(Collectors.toList());
                    ansjUtil.addWord(root.getTenantId(), root.getProjectId(), addWords);
                }
            } else {
                //下线词库中进行移除,基于当前在线和下线中的词根重新生成文件
                ansjUtil.rebuildDic(root.getTenantId(), root.getProjectId());
            }
        }
    }

    /**
     * 版本记录
     *
     * @param root
     */
    private void doVersion(Root root) {
        Long versionNum = DEFAULT_VERSION;
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootVersion.FIELD_ROOT_ID, root.getId()))
                .orderByDesc(RootVersion.FIELD_VERSION_NUMBER)
                .build());
        if (CollectionUtils.isNotEmpty(rootVersions)) {
            versionNum = rootVersions.get(0).getVersionNumber() + 1;
        }
        root = rootRepository.list(root).get(0);
        RootVersion rootVersion = new RootVersion();
        BeanUtils.copyProperties(root, rootVersion);
        rootVersion.setId(null);
        rootVersion.setRootId(root.getId());
        rootVersion.setVersionNumber(versionNum);
        rootVersionRepository.insert(rootVersion);
    }

    private void startWorkFlow(String workflowKey, Root root, String status) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        StandardApprovalDTO standardApprovalDTO = StandardApprovalDTO
                .builder()
                .standardId(root.getId())
                .standardType("ROOT")
                .applicantId(userId)
                .applyType(status)
                .tenantId(root.getTenantId())
                .build();
        // 先删除原来的审批记录
        standardApprovalService.delete(StandardApprovalDTO
                .builder()
                .standardId(root.getId())
                .standardType("ROOT")
                .applicantId(userId)
                .build());
        standardApprovalDTO = standardApprovalService.createOrUpdate(standardApprovalDTO);
        //使用当前时间戳作为业务主键
        String bussinessKey = String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("rootId", root.getId());
        var.put("approvalId", standardApprovalDTO.getApprovalId());
        //使用自研工作流客户端
        RunInstance runInstance = workflowClient.startInstanceByFlowKey(root.getTenantId(), workflowKey, bussinessKey, "USER", String.valueOf(userId), var);
        if (Objects.nonNull(runInstance)) {
            standardApprovalDTO.setApplyTime(runInstance.getStartDate());
            standardApprovalDTO.setInstanceId(runInstance.getInstanceId());
            standardApprovalService.createOrUpdate(standardApprovalDTO);
        }
    }
}
