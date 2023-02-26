package cn.grokit.dailyspot.entity.zhihu;


import lombok.Data;

/**
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Data
@SuppressWarnings("unused")
public class Paging {

    private boolean isEnd;
    private String next;
    private String previous;

}
