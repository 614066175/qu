package org.xdsp.quality.infra.converttype;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description
 *
 * @author 23168 2021/08/27 11:28
 */
@Component("ORACLE")
public class ConvertTypeOracle extends ConvertTypeBase{

    @Override
    public List<String> typeConvert(String type, List<String> list) {
        String[] splits = type.split(",");
        for (int i = 0; i < splits.length; i++) {
            splits[i] = splits[i].substring(splits[i].indexOf("(") + 1, splits[i].indexOf(")"));
            switch (splits[i].toUpperCase()) {
                case "VARCHAR2":
                    list.add("STRING");
                    break;
                case "NUMBER":
                    list.add("DECIMAL");
                    break;
                default:
                    super.typeConvert(splits[i].toUpperCase(), list);
            }
        }
        return list;
    }
}
