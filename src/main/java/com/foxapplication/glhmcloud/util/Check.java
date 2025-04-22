package com.foxapplication.glhmcloud.util;

public class Check {

    public static boolean checkPassword(String password) {
        // 修改:密码长度必须大于8位
        if (password.length() < 9) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
            if (Character.isLowerCase(c)) {
                hasLower = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            // 修改:检测特殊字符
            if ("!@#$%^&*(),.?\":{}|<>".indexOf(c) >= 0) {
                hasSpecial = true;
            }
        }

        int count = 0;
        if (hasUpper) count++;
        if (hasLower) count++;
        if (hasDigit) count++;
        if (hasSpecial) count++;

        // 修改:要求至少包含三种字符类型
        return count >= 3;
    }
}
