package com.xe.lzh.rfid.Utils;



import java.util.List;

/**
 * Created by yf on 2016/1/30.
 */
public class ZhuangTaiUtils {

    /**
     * 根据订单状态码返回字符串类型的订单状态
     *
     * @param code
     * @return
     */
    public static String transOrderStatusCode(int code) {
        for (OrderStatusEnum item : OrderStatusEnum.values()) {
            if (item.key == code) {
                return item.value;
            }
        }
        return "";
    }
    public enum OrderStatusEnum {
        WAITING_ORDER_RECEIVING(1, "已入库"),
        Order_Dealing(2, "已出库"),
        WAITING_FOR_PAY(3, "已借出"),
        WAITING_FOR_SHIPMENTS(-1, "未打印标签"),
        YI_DA_YIN_BIAOQIAN(0,"已打印标签");


        public final int key;
        public final String value;

        OrderStatusEnum(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }


}
