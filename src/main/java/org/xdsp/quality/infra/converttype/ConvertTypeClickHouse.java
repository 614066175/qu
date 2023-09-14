package org.xdsp.quality.infra.converttype;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author 23168 2021/08/27 11:31
 */
@Component("CLICKHOUSE")
public class ConvertTypeClickHouse extends ConvertTypeBase{
	@Override
	public List<String> typeConvert(String type, List<String> list) {
		String[] splits = type.split(",");
		for (int i = 0; i < splits.length; i++) {
			splits[i] = splits[i].substring(splits[i].indexOf("(") + 1, splits[i].indexOf(")"));
			switch (splits[i].toUpperCase()) {
				case "INT32":
					list.add("INTEGER");
					break;
				default:
					super.typeConvert(splits[i].toUpperCase(), list);
			}
		}
		return list;
	}
}
