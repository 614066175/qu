package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.ReferenceDataHistoryService;
import com.hand.hdsp.quality.domain.entity.ReferenceDataHistory;
import com.hand.hdsp.quality.api.dto.ReferenceDataHistoryDTO;
import com.hand.hdsp.quality.domain.repository.ReferenceDataHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>参考数据头表 管理 API</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@RestController("referenceDataHistoryController.v1")
@RequestMapping("/v1/{organizationId}/reference-data-historys")
public class ReferenceDataHistoryController extends BaseController {

    private ReferenceDataHistoryRepository referenceDataHistoryRepository;
    private final ReferenceDataHistoryService referenceDataHistoryService;

    public ReferenceDataHistoryController(ReferenceDataHistoryRepository referenceDataHistoryRepository,
                                          ReferenceDataHistoryService referenceDataHistoryService) {
        this.referenceDataHistoryRepository = referenceDataHistoryRepository;
        this.referenceDataHistoryService = referenceDataHistoryService;
    }

    @ApiOperation(value = "参考数据头表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                ReferenceDataHistoryDTO referenceDataHistoryDTO, @ApiIgnore @SortDefault(value = ReferenceDataHistory.FIELD_VERSION_NUMBER,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<ReferenceDataHistoryDTO> list = referenceDataHistoryService.list(projectId, tenantId, referenceDataHistoryDTO, pageRequest);
        return Results.success(list);
    }

    @ApiOperation(value = "参考数据头表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "historyId",
            value = "参考数据头表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{historyId}")
    public ResponseEntity<?> detail(@PathVariable Long historyId) {
        ReferenceDataHistoryDTO referenceDataHistoryDTO = referenceDataHistoryService.detail(historyId);
        return Results.success(referenceDataHistoryDTO);
    }


    @ApiOperation(value = "删除参考数据头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody ReferenceDataHistoryDTO referenceDataHistoryDTO) {
        referenceDataHistoryService.remove(projectId, tenantId, referenceDataHistoryDTO);
        return Results.success();
    }
}
