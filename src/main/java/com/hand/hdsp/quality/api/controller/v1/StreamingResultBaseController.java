package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.StreamingResultBaseDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;
import com.hand.hdsp.quality.domain.repository.StreamingResultBaseRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>实时数据方案结果表-基础信息 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Api(tags = SwaggerTags.STREAMING_RESULT_BASE)
@RestController("streamingResultBaseController.v1")
@RequestMapping("/v1/{organizationId}/streaming-result-bases")
public class StreamingResultBaseController extends BaseController {

    private final StreamingResultBaseRepository streamingResultBaseRepository;

    public StreamingResultBaseController(StreamingResultBaseRepository streamingResultBaseRepository) {
        this.streamingResultBaseRepository = streamingResultBaseRepository;
    }

    @ApiOperation(value = "实时数据方案结果表-基础信息列表")
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
                                  StreamingResultBaseDTO streamingResultBaseDTO, @ApiIgnore @SortDefault(value = StreamingResultBase.FIELD_RESULT_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingResultBaseDTO.setTenantId(tenantId);
        streamingResultBaseDTO.setProjectId(projectId);
        Page<StreamingResultBaseDTO> list = streamingResultBaseRepository.pageAndSortDTO(pageRequest, streamingResultBaseDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "评估结果基础信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/result-base")
    public ResponseEntity<?> reseultBase(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         StreamingResultBaseDTO streamingResultBaseDTO) {
        streamingResultBaseDTO.setTenantId(tenantId);
        streamingResultBaseDTO.setProjectId(projectId);
        return Results.success(streamingResultBaseRepository.listResultBase(streamingResultBaseDTO));
    }

    @ApiOperation(value = "实时数据方案结果表-基础信息明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultBaseId",
            value = "实时数据方案结果表-基础信息主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultBaseId}")
    public ResponseEntity<?> detail(@PathVariable Long resultBaseId) {
        StreamingResultBaseDTO streamingResultBaseDTO = streamingResultBaseRepository.selectDTOByPrimaryKeyAndTenant(resultBaseId);
        return Results.success(streamingResultBaseDTO);
    }

    @ApiOperation(value = "创建实时数据方案结果表-基础信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody StreamingResultBaseDTO streamingResultBaseDTO) {
        streamingResultBaseDTO.setTenantId(tenantId);
        streamingResultBaseDTO.setProjectId(projectId);
        this.validObject(streamingResultBaseDTO);
        streamingResultBaseRepository.insertDTOSelective(streamingResultBaseDTO);
        return Results.success(streamingResultBaseDTO);
    }

    @ApiOperation(value = "修改实时数据方案结果表-基础信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody StreamingResultBaseDTO streamingResultBaseDTO) {
        streamingResultBaseDTO.setProjectId(projectId);
        streamingResultBaseRepository.updateDTOAllColumnWhereTenant(streamingResultBaseDTO, tenantId);
        return Results.success(streamingResultBaseDTO);
    }

    @ApiOperation(value = "删除实时数据方案结果表-基础信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StreamingResultBaseDTO streamingResultBaseDTO) {
        streamingResultBaseDTO.setTenantId(tenantId);
        streamingResultBaseRepository.deleteByPrimaryKey(streamingResultBaseDTO);
        return Results.success();
    }
}
