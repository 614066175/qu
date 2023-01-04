package com.hand.hdsp.quality.infra.converttype;

import java.util.List;

/**
 * 类型转换
 *
 * @author 29713 2021/08/27 10:37
 */
public class ConvertTypeBase {

    public List<String> typeConvert(String type, List<String> list) {
        switch (type) {
            case "VARCHAR":
            case "TEXT":
            case "STRING":
                list.add("STRING");
                break;
            case "BIGINT":
                list.add("BIGINTEGER");
                break;
            case "INT":
            case "SMALLINT":
            case "TINYINT":
                list.add("INTEGER");
                break;
            case "DATETIME":
            case "TIMESTAMP":
                list.add("DATETIME");
                break;
            case "DECIMAL":
            case "NUMERIC":
            case "FLOAT":
                list.add("DECIMAL");
                break;
            case "DATE":
                list.add("DATE");
                break;
            default:
                //默认不做处理
                list.add(type);
//                throw new CommonException(ErrorCode.UNKNOWN_DATATYPE);
        }
        return list;
    }

}
