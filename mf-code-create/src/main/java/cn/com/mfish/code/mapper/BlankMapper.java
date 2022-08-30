package cn.com.mfish.code.mapper;

import cn.com.mfish.code.entity.Blank;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ：qiufeng
 * @description：空mapper防止mybatis-plus启动时扫不到mapper报错
 * @date ：2022/8/24 16:28
 */
@Mapper
public interface BlankMapper extends BaseMapper<Blank> {
}
