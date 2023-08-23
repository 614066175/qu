package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.quality.domain.entity.RootLine;
import org.xdsp.quality.domain.repository.RootLineRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 词根中文名行表 管理 API
 *
 * @author xin.sheng01@china-hand.com 2022-11-22 15:37:15
 */
@RestController("rootLineController.v1")
@RequestMapping("/v1/{organizationId}/root-lines")
public class RootLineController extends BaseController {

    @Autowired
    private RootLineRepository rootLineRepository;

    @ApiOperation(value = "词根中文名行表列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RootLine>> list(RootLine rootLine, @ApiIgnore @SortDefault(value = RootLine.FIELD_ROOT_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<RootLine> list = rootLineRepository.pageAndSort(pageRequest, rootLine);
        return Results.success(list);
    }

    @ApiOperation(value = "词根中文名行表明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{rootLineId}")
    public ResponseEntity<RootLine> detail(@PathVariable Long rootLineId) {
        RootLine rootLine = rootLineRepository.selectByPrimaryKey(rootLineId);
        return Results.success(rootLine);
    }

    @ApiOperation(value = "创建词根中文名行表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<RootLine> create(@RequestBody RootLine rootLine) {
        validObject(rootLine);
        rootLineRepository.insertSelective(rootLine);
        return Results.success(rootLine);
    }

    @ApiOperation(value = "修改词根中文名行表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<RootLine> update(@RequestBody RootLine rootLine) {
        SecurityTokenHelper.validToken(rootLine);
        rootLineRepository.updateByPrimaryKeySelective(rootLine);
        return Results.success(rootLine);
    }

    @ApiOperation(value = "删除词根中文名行表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody RootLine rootLine) {
        SecurityTokenHelper.validToken(rootLine);
        rootLineRepository.deleteByPrimaryKey(rootLine);
        return Results.success();
    }

}
