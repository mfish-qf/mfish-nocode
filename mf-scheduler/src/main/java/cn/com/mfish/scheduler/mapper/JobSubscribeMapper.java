package cn.com.mfish.scheduler.mapper;

import cn.com.mfish.scheduler.entity.JobSubscribe;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V1.0.0
 */
public interface JobSubscribeMapper extends BaseMapper<JobSubscribe> {
    int insertJobSubscribes(@Param("jobSubscribeList") List<JobSubscribe> jobSubscribeList);

    List<JobSubscribe> getSubscribesByJobIds(@Param("jobIds") List<String> jobIds);
}
