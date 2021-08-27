package com.hand.hdsp.quality.infra.converttype;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description
 *
 * @author 23168 2021/08/27 11:31
 */
@Component("SQLSERVER")
public class ConvertTypeSqlServer extends ConvertTypeBase{

    @Override
    public List<String> typeConvert(String type, List<String> list) {
        String[] splits = type.split(",");
        for (int i = 0; i < splits.length; i++) {
            splits[i] = splits[i].substring(splits[i].indexOf("(") + 1, splits[i].indexOf(")"));
            //SQL_SERVER暂时没有特殊的字段类型，直接调用父方法
            super.typeConvert(splits[i].toUpperCase(), list);
        }
        return list;
    }
}
