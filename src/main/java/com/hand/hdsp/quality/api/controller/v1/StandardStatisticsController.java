package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;
import com.hand.hdsp.quality.app.service.StandardStatisticsService;
import com.hand.hdsp.quality.domain.entity.StandardStatistics;
import com.hand.hdsp.quality.domain.repository.StandardStatisticsRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p> 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
@RestController("standardStatisticsController.v1")
@RequestMapping("/v1/{organizationId}/standard-statisticss")
public class StandardStatisticsController extends BaseController {

    private StandardStatisticsRepository standardStatisticsRepository;
    private StandardStatisticsService standardStatisticsService;

    public StandardStatisticsController(StandardStatisticsRepository standardStatisticsRepository,StandardStatisticsService standardStatisticsService) {
        this.standardStatisticsRepository = standardStatisticsRepository;
        this.standardStatisticsService=standardStatisticsService;
    }

    @ApiOperation(value = "标准落标表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                StandardStatisticsDTO standardStatisticsDTO, @ApiIgnore @SortDefault(value = StandardStatistics.FIELD_STATISTICS_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardStatisticsDTO.setTenantId(tenantId);
        Page<StandardStatisticsDTO> list = standardStatisticsRepository.pageAndSortDTO(pageRequest, standardStatisticsDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准落标表列表模糊查询")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-all")
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                  StandardStatisticsDTO standardStatisticsDTO) {
        standardStatisticsDTO.setTenantId(tenantId);
        return Results.success(standardStatisticsService.listAll(standardStatisticsDTO));
    }

    @ApiOperation(value = "标准落标表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "statisticsId",
            value = "标准落标表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{statisticsId}")
    public ResponseEntity<?> detail(@PathVariable Long statisticsId) {
        StandardStatisticsDTO standardStatisticsDTO = standardStatisticsRepository.selectDTOByPrimaryKeyAndTenant(statisticsId);
        return Results.success(standardStatisticsDTO);
    }

    @ApiOperation(value = "创建标准落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StandardStatisticsDTO standardStatisticsDTO) {
        standardStatisticsDTO.setTenantId(tenantId);
        this.validObject(standardStatisticsDTO);
        standardStatisticsRepository.insertDTOSelective(standardStatisticsDTO);
        return Results.success(standardStatisticsDTO);
    }

    @ApiOperation(value = "修改标准落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StandardStatisticsDTO standardStatisticsDTO) {
                standardStatisticsRepository.updateDTOWhereTenant(standardStatisticsDTO, tenantId);
        return Results.success(standardStatisticsDTO);
    }

    @ApiOperation(value = "删除标准落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardStatisticsDTO standardStatisticsDTO) {
                standardStatisticsDTO.setTenantId(tenantId);
        standardStatisticsRepository.deleteByPrimaryKey(standardStatisticsDTO);
        return Results.success();
    }
}
