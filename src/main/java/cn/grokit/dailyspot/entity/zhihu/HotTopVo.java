package cn.grokit.dailyspot.entity.zhihu;

import lombok.Data;

import java.util.List;

/**
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Data
public class HotTopVo {
    private List<Datum> data;
    private long displayNum;
    private String freshText;
    private Paging paging;
}
