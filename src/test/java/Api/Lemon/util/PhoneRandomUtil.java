package Api.Lemon.util;

import java.util.Random;

public class PhoneRandomUtil {
    public static void main(String[] args) {
        System.out.println(getUnregisterPhone());
    }

    public static String getRandomPhone(){
        Random random = new Random();
        String phonePrefix = "151";
/*        //生成8位随机数，再与151拼接
        int num = random.nextInt(99999999);
        System.out.println(phonePrefix +num);*/
        for (int i = 0; i < 8; i++) {
            int num = random.nextInt(9);
            phonePrefix = phonePrefix + num;
        }
        System.out.println(phonePrefix);
        return phonePrefix;
    }

    public static String getUnregisterPhone() {
        String phone = "";
        while (true) {
            phone = getRandomPhone();
            Object result = JDBCUtils.querySingleData("SELECT count(*) FROM `futureloan`.`member` WHERE mobile_phone=" + phone);
            if ((Long) result == 0) {
                //表示手机号没有被注册，符合需求，结束循环
                break;
            }
            continue; //表示手机号已被注册，继续执行上述过程
        }
        return phone;
    }
}