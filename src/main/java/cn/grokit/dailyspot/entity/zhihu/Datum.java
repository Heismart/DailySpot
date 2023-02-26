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
public class Datum {
    private String attachedInfo;
    private String cardId;
    private List<Child> children;
    private boolean debut;
    private String detailText;
    private String id;
    private String styleType;
    private Target target;
    private long trend;
    private String type;

}
