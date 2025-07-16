package com.voracityrat.memehubbackend.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片审核状态枚举类
 *
 * @author grey
 */

@AllArgsConstructor
@Getter
public enum PictureReviewEnum {

    REVIEWING("待审核", 0),
    PASS("审核通过", 1),
    REJECT("审核驳回", 2),
    ILLEGAL("违规下架", 3);


    private final String text;
    private final int value;

    /**
     * 根据状态码获取对应中文称谓
     *
     * @param value
     * @return
     */
    public static String getReviewTextByValue(Integer value) {

        if (value == null) {
            return null;
        }

        for (PictureReviewEnum pictureReviewEnum : PictureReviewEnum.values()) {
            if (pictureReviewEnum.value == value) {
                return pictureReviewEnum.text;
            }
        }
        return null;
    }

    /**
     * 根据角色编码获取到对应角色的枚举类
     *
     * @param value
     * @return
     */
    public static PictureReviewEnum getReviewEnumByValue(Integer value) {

        if (value == null) {
            return null;
        }

        for (PictureReviewEnum pictureReviewEnum : PictureReviewEnum.values()) {
            if (pictureReviewEnum.value == value) {
                return pictureReviewEnum;
            }
        }
        return null;
    }


}
