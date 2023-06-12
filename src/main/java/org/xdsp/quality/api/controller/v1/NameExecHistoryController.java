package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.NameExecHistoryDTO;
import org.xdsp.quality.domain.repository.NameExecHistoryRepository;
import org.xdsp.quality.infra.vo.NameStandardHisReportVO;

import java.util.List;

/**
 * <p>命名标准执行历史表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameExecHistoryController.v1")
@RequestMapping("/v1/{organizationId}/name-exec-historys")
public class NameExecHistoryController extends BaseController {

    private final NameExecHistoryRepository nameExecHistoryRepository;

    public NameExecHistoryController(NameExecHistoryRepository nameExecHistoryRepository) {
        this.nameExecHistoryRepository = nameExecHistoryRepository;
    }

    @ApiOperation(value = "命名标准执行历史表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<Page<NameExecHistoryDTO>> list(NameExecHistoryDTO nameExecHistoryDTO,
                                                         @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                         PageRequest pageRequest) {
        nameExecHistoryDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<NameExecHistoryDTO> list = PageHelper.doPage(pageRequest,
                ()->nameExecHistoryRepository.getHistoryList(nameExecHistoryDTO));
        return Results.success(list);
    }
    @ApiOperation(value = "命名标准执行历史表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "historyId",
            value = "命名标准执行历史表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{historyId}")
    public ResponseEntity<NameExecHistoryDTO> detail(@PathVariable Long historyId) {
        NameExecHistoryDTO nameExecHistoryDTO = nameExecHistoryRepository.detail(historyId);
        return Results.success(nameExecHistoryDTO);
    }

    @ApiOperation(value = "最新执行结果")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "standardId",
            value = "命名标准主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/latest/{standardId}")
    public ResponseEntity<NameExecHistoryDTO> latest(@PathVariable Long standardId) {
        NameExecHistoryDTO nameExecHistoryDTO = nameExecHistoryRepository.getLatestHistory(standardId);
        return Results.success(nameExecHistoryDTO);
    }

    @ApiOperation(value = "获取标准执行报表信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "standardId",
            value = "命名标准主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/report/{standardId}")
    public ResponseEntity<List<NameStandardHisReportVO>> report(@PathVariable Long standardId) {
        List<NameStandardHisReportVO> nameStandardHisReportVOList = nameExecHistoryRepository.getReport(standardId);
        return Results.success(nameStandardHisReportVOList);
    }
}
