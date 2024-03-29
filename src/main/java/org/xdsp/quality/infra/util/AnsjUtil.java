package org.xdsp.quality.infra.util;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.imported.infra.util.FileUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xdsp.core.lock.RedisLockHelper;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.domain.entity.RootDic;
import org.xdsp.quality.domain.entity.RootLine;
import org.xdsp.quality.domain.repository.RootDicRepository;
import org.xdsp.quality.domain.repository.RootLineRepository;
import org.xdsp.quality.domain.repository.RootRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.publisher.DicChangeNoticePublisher;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.ONLINE;

/**
 * @Title: AnsjUtil
 * @Description:
 * @author: lgl
 * @date: 2022/12/5 20:39
 */
@Slf4j
@Component
public class AnsjUtil {
    public static final String DIC_FORMAT = "%s\tn\t1\n";

    public static final String DIC_BUCKET = "root-dic";

    private final FileClient fileClient;
    private final RootRepository rootRepository;
    private final RootLineRepository rootLineRepository;
    private final RootDicRepository rootDicRepository;
    private final RedisLockHelper redisLockHelper;

    @Value("${xdsp.root_dic_parent:library}")
    public String dicParent;

    public static final String DIC_NAME_FORMAT = "rootLibrary_%d_%d.dic";

    private final DicChangeNoticePublisher dicChangeNoticePublisher;


    public AnsjUtil(FileClient fileClient, RootRepository rootRepository, RootLineRepository rootLineRepository, RootDicRepository rootDicRepository, RedisLockHelper redisLockHelper, DicChangeNoticePublisher dicChangeNoticePublisher) {
        this.fileClient = fileClient;
        this.rootRepository = rootRepository;
        this.rootLineRepository = rootLineRepository;
        this.rootDicRepository = rootDicRepository;
        this.redisLockHelper = redisLockHelper;
        this.dicChangeNoticePublisher = dicChangeNoticePublisher;
    }

    /**
     * 自定义词库动态追加词
     *
     * @param tenantId
     * @param projectId
     * @param words
     */
    public void addWord(Long tenantId, Long projectId, List<String> words) {
        //上传文件服务器,多实例时保证各个服务文件统一性
        //获取当前目录下对应词库
        File library = new File(dicParent);
        if (!library.exists()) {
            library.mkdir();
        }
        String dicName = String.format(DIC_NAME_FORMAT, tenantId, projectId);
        //词典操作上锁,避免数据不一致
        redisLockHelper.execute(dicName, 30L, TimeUnit.SECONDS, () -> {
            File rootLibrary = new File(library, dicName);
            org.apache.commons.io.FileUtils.deleteQuietly(rootLibrary);
            //判断文件服务器上是否存在此租户此项目的词库
            downloadDic(tenantId, projectId, rootLibrary);
            RootDic rootDic = rootDicRepository.selectOne(RootDic.builder().tenantId(tenantId).projectId(projectId).build());
            if (!rootLibrary.exists()) {
                //直接创建当前词库文件
                try {
                    rootLibrary.createNewFile();
                } catch (IOException e) {
                    throw new CommonException(ErrorCode.DIY_DIC_CREATE_ERROR, e);
                }
            }
            if (CollectionUtils.isEmpty(words)) {
                log.info("新增词组为空");
                return;
            }
            try (FileWriter fileWriter = new FileWriter(rootLibrary, true)) {
                for (String word : words) {
                    //文件追加
                    fileWriter.write(String.format(DIC_FORMAT, word));
                    fileWriter.flush();
                }
            } catch (IOException e) {
                throw new CommonException(ErrorCode.DIC_APPEND_ERROR);
            }
            //上传minio
            try (FileInputStream inputStream = new FileInputStream(rootLibrary)) {
                String dicUrl = fileClient.uploadFile(tenantId, DIC_BUCKET, null, rootLibrary.getName(), FileUtils.inputStreamToByteArray(inputStream));
                if (rootDic != null) {
                    String oldUrl = rootDic.getDicUrl();
                    fileClient.deleteFileByUrl(tenantId, DIC_BUCKET, Collections.singletonList(oldUrl));
                    rootDic.setDicUrl(dicUrl);
                    rootDicRepository.updateOptional(rootDic, RootDic.FIELD_DIC_URL);
                } else {
                    RootDic newDic = RootDic.builder()
                            .dicName(rootLibrary.getName())
                            .dicUrl(dicUrl)
                            .tenantId(tenantId)
                            .projectId(projectId)
                            .build();
                    rootDicRepository.insertSelective(newDic);
                }
            } catch (Exception e) {
                throw new CommonException(ErrorCode.DIC_FILE_UPLOAD_FAILED,e);
            }
        });
        //通知各服务删除此变化的词库文件,等到下次使用时去下载最新的
        dicChangeNoticePublisher.sendDicChangeNotice(dicName);
    }


    /**
     * 自定义词库动态追加词
     *
     * @param tenantId
     * @param projectId
     * @param words
     */
    public void rebuildDic(Long tenantId, Long projectId, List<String> words) {
        if (CollectionUtils.isEmpty(words)) {
            log.info("词未空，无需构建词库文件");
            return;
        }
        //上传文件服务器,多实例时保证各个服务文件统一性
        //获取当前目录下对应词库
        File library = new File(dicParent);
        if (!library.exists()) {
            library.mkdir();
        }
        String dicName = String.format(DIC_NAME_FORMAT, tenantId, projectId);
        //词典操作上锁,避免数据不一致
        redisLockHelper.execute(dicName, 30L, TimeUnit.SECONDS, () -> {
            File rootLibrary = new File(library, dicName);
            //删除本地可能存在的旧词库文件
            org.apache.commons.io.FileUtils.deleteQuietly(rootLibrary);
            //重新创建dic文件
            if (!rootLibrary.exists()) {
                //直接创建当前词库文件
                try {
                    rootLibrary.createNewFile();
                } catch (IOException e) {
                    throw new CommonException(ErrorCode.DIY_DIC_CREATE_ERROR, e);
                }
            }
            try (FileWriter fileWriter = new FileWriter(rootLibrary, true)) {
                for (String word : words) {
                    //文件追加
                    fileWriter.write(String.format(DIC_FORMAT, word));
                    fileWriter.flush();
                }
            } catch (IOException e) {
                throw new CommonException(ErrorCode.DIC_APPEND_ERROR);
            }
            //上传minio
            try (FileInputStream inputStream = new FileInputStream(rootLibrary)) {
                String dicUrl = fileClient.uploadFile(tenantId, DIC_BUCKET, null, rootLibrary.getName(), FileUtils.inputStreamToByteArray(inputStream));
                RootDic rootDic = rootDicRepository.selectOne(RootDic.builder().tenantId(tenantId).projectId(projectId).build());
                if (rootDic != null) {
                    String oldUrl = rootDic.getDicUrl();
                    fileClient.deleteFileByUrl(tenantId, DIC_BUCKET, Collections.singletonList(oldUrl));
                    rootDic.setDicUrl(dicUrl);
                    rootDicRepository.updateOptional(rootDic, RootDic.FIELD_DIC_URL);
                } else {
                    RootDic newDic = RootDic.builder()
                            .dicName(rootLibrary.getName())
                            .dicUrl(dicUrl)
                            .tenantId(tenantId)
                            .projectId(projectId)
                            .build();
                    rootDicRepository.insertSelective(newDic);
                }
            } catch (Exception e) {
                throw new CommonException(ErrorCode.DIC_FILE_UPLOAD_FAILED);
            }
        });
        //通知各服务删除此变化的词库文件,等到下次使用时去下载最新的
        dicChangeNoticePublisher.sendDicChangeNotice(dicName);
    }

    public void rebuildDic(Long tenantId, Long projectId) {
        //查询在线,下线中的词根
        List<Root> roots = rootRepository.selectByCondition(Condition.builder(Root.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Root.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(Root.FIELD_PROJECT_ID, projectId)
                        .andIn(Root.FIELD_RELEASE_STATUS, Arrays.asList(ONLINE, OFFLINE_APPROVING)))
                .build());
        if (CollectionUtils.isEmpty(roots)) {
            //没有词根，删除词库文件
            RootDic rootDic = rootDicRepository.selectOne(RootDic.builder().tenantId(tenantId).projectId(projectId).build());
            if (rootDic != null) {
                //删除minio文件
                fileClient.deleteFileByUrl(tenantId, DIC_BUCKET, Arrays.asList(rootDic.getDicUrl()));
                rootDicRepository.deleteByPrimaryKey(rootDic);
            }
            return;
        }
        List<Long> rootIds = roots.stream().map(Root::getId).collect(Collectors.toList());
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andIn(RootLine.FIELD_ROOT_ID, rootIds))
                .build());
        if (CollectionUtils.isEmpty(rootLines)) {
            //词根中文是必填项，此不会为空，健壮性判断
            return;
        }
        List<String> words = rootLines.stream()
                .filter(rootLine -> Strings.isNotBlank(rootLine.getRootName()))
                .map(RootLine::getRootName)
                .distinct()
                .collect(Collectors.toList());
        rebuildDic(tenantId, projectId, words);
    }

    public void downloadDic(Long tenantId, Long projectId, File dic) {
        RootDic rootDic = rootDicRepository.selectOne(RootDic.builder().tenantId(tenantId).projectId(projectId).build());
        if (rootDic != null) {
            try (InputStream inputStream = fileClient.downloadFile(tenantId, DIC_BUCKET, rootDic.getDicUrl())) {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, dic);
            } catch (IOException e) {
                throw new CommonException(ErrorCode.DIC_FILE_DOWNLOAD_FAILED);
            }
        }
    }

    public void deleteDicFile(String fileName) {
        File library = new File(dicParent);
        if (!library.exists()) {
            //目录不存在直接返回
            return;
        }
        File file = new File(library, fileName);
        org.apache.commons.io.FileUtils.deleteQuietly(file);
    }

    public void deleteDicFiles() {
        File library = new File(dicParent);
        if (!library.exists()) {
            //目录不存在直接返回
            return;
        }
        org.apache.commons.io.FileUtils.deleteQuietly(library);
    }

    public File getDic(Long tenantId, Long projectId) {
        //获取
        File library = new File(dicParent);
        if (!library.exists()) {
            library.mkdir();
        }
        File rootLibrary = new File(library, String.format(DIC_NAME_FORMAT, tenantId, projectId));
        if (rootLibrary.exists()) {
            return rootLibrary;
        } else {
            downloadDic(tenantId, projectId, rootLibrary);
        }
        return rootLibrary;
    }
}
