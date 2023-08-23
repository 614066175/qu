package org.xdsp.quality.infra.converttype;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description
 *
 * @author 23168 2021/08/27 11:29
 */
@Component("HIVE2")
public class ConvertTypeHive2 extends ConvertTypeBase{

    @Override
    public List<String> typeConvert(String type, List<String> list) {
        String[] splits = type.split(",");
        for (int i = 0; i < splits.length; i++) {
            splits[i] = splits[i].substring(splits[i].indexOf("(") + 1, splits[i].indexOf(")"));
            //表中现存字段类型没有特殊类型，默认调用父类方法
            super.typeConvert(splits[i].toUpperCase(), list);
        }
        return list;
    }
}
