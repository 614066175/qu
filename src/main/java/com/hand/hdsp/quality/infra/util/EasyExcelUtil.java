package com.hand.hdsp.quality.infra.util;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2019/08/26 9:59
 * @since 1.0
 */
@SuppressWarnings("unused")
@Slf4j
public class EasyExcelUtil {


    private EasyExcelUtil() {
        throw new IllegalStateException("util class!");
    }


    /**
     * 读取excel
     *
     * @param is            输入文件的流对象
     * @param sheetNo       第几个sheet
     * @param excelListener excel的监听类
     * @author isacc 2019/8/26 10:05
     */
    @SuppressWarnings("rawtypes")
    public static void readExcelCommon(InputStream is, int sheetNo, AnalysisEventListener excelListener) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            // 解析每行结果在listener中处理
            ExcelReader excelReader = EasyExcelFactory.getReader(bis, excelListener);
            // sheetNo, 第二个1代表从第几行开始读取数据，行号最小值为0
            excelReader.read(new Sheet(sheetNo, 1));
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_READ_ERROR, e);
        }
    }

    /**
     * 读取excel
     *
     * @param in            输入文件的流对象
     * @param sheetNo       第几个sheet
     * @param excelListener excel的监听类
     * @author isacc 2019/8/26 10:05
     */
    public static void readExcelSax(InputStream in, int sheetNo, AnalysisEventListener excelListener) {
        try (BufferedInputStream bis = new BufferedInputStream(in)) {
            // 解析每行结果在listener中处理
            // sheetNo, 第二个1代表从第几行开始读取数据，行号最小值为0
            EasyExcelFactory.readBySax(in, new Sheet(sheetNo, 1), excelListener);
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_READ_ERROR, e);
        }
    }

    /**
     * 读取excel
     *
     * @param is            输入文件的流对象
     * @param clazz         Excel实体映射类
     * @param sheetNo       第几个sheet
     * @param excelListener excel的监听类
     * @return java.lang.Boolean 是否成功, 如果成功可以再监听类里面取数据
     * @author isacc 2019/8/26 10:05
     */
    public static Boolean readExcel(InputStream is, Class<? extends BaseRowModel> clazz, int sheetNo, AnalysisEventListener excelListener) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            // 解析每行结果在listener中处理
            ExcelReader excelReader = EasyExcelFactory.getReader(bis, excelListener);
            // sheetNo, 第二个1代表从第几行开始读取数据，行号最小值为0
            excelReader.read(new Sheet(sheetNo, 1, clazz));
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_READ_ERROR, e);
        }
        return true;
    }

    /**
     * 读取excel
     *
     * @param is      输入文件的流对象
     * @param sheetNo 第几个sheet
     * @author isacc 2019/8/26 10:05
     */
    public static List<Object> readExcelLess(InputStream is, int sheetNo) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            // 解析每行结果在listener中处理
            return EasyExcelFactory.read(bis, new Sheet(sheetNo, 0));
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_READ_ERROR, e);
        }
    }

    /**
     * 写excel
     *
     * @param os      文件输出流
     * @param clazz   Excel实体映射类
     * @param data    导出数据
     * @param sheetNo 第几个sheet
     * @author isacc 2019/8/26 10:06
     */
    public static void writeExcel(OutputStream os, Class<? extends BaseRowModel> clazz, List<? extends BaseRowModel> data, int sheetNo) {
        try (BufferedOutputStream bos = new BufferedOutputStream(os)) {
            ExcelWriter writer = new ExcelWriter(bos, ExcelTypeEnum.XLSX);
            //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
            Sheet sheet1 = new Sheet(sheetNo, 0, clazz);
            writer.write(data, sheet1);
            writer.finish();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_READ_ERROR, e);
        }
    }

    /**
     * excel导出
     *
     * @param os         文件输出流
     * @param herderList excel头数据
     * @param sheetName  sheet名称
     * @param sheetNo    第几个sheet
     * @author isacc 2019/8/26 10:06
     */
    public static void writeExcel(OutputStream os,
                                  List<List<String>> herderList,
                                  List<List<Object>> dataList,
                                  String sheetName,
                                  int sheetNo) {
        try (BufferedOutputStream bos = new BufferedOutputStream(os)) {
            ExcelWriter writer = new ExcelWriter(bos, ExcelTypeEnum.XLSX, true);
            Sheet sheet1 = new Sheet(sheetNo, 0);
            sheet1.setSheetName(sheetName);
            sheet1.setHead(herderList);
            writer.write1(dataList, sheet1);
            writer.finish();
            bos.flush();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_WRITE_ERROR, e);
        }
    }

    /**
     * 写excel模板
     *
     * @param os         文件输出流
     * @param herderList excel头数据
     * @param sheetName  sheet名称
     * @param sheetNo    第几个sheet
     * @author isacc 2019/8/26 10:06
     */
    public static void writeTemplateExcel(OutputStream os, List<List<String>> herderList, String sheetName, int sheetNo) {
        try (BufferedOutputStream bos = new BufferedOutputStream(os)) {
            ExcelWriter writer = new ExcelWriter(bos, ExcelTypeEnum.XLSX, true);
            Sheet sheet1 = new Sheet(sheetNo, 0);
            sheet1.setSheetName(sheetName);
            sheet1.setHead(herderList);
            writer.write1(null, sheet1);
            writer.finish();
            bos.flush();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_WRITE_ERROR, e);
        }
    }

}