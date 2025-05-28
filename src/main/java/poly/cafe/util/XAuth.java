/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.util;

import poly.cafe.entity.Users;

public class XAuth {
    public static Users user = null;

    public static void setUser(Users user) {
        XAuth.user = user;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static void clear() {
        user = null;
    }
}