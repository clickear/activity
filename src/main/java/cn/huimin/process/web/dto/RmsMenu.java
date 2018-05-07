package cn.huimin.process.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyp on 2017/9/27.
 */
public class RmsMenu implements Serializable {
    private Integer menu_id;
    private Integer menu_pid;
    private String remark;
    private String menu_name;
    //菜单跳转地址
    private String per_action;
    //菜单控制按钮
    private String per_controller;

    private String ico_view;
    /**
     * 获取子菜单
     */
    private List<RmsMenu> menuArray = new ArrayList<>(0);


    public Integer getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Integer menu_id) {
        this.menu_id = menu_id;
    }

    public Integer getMenu_pid() {
        return menu_pid;
    }

    public void setMenu_pid(Integer menu_pid) {
        this.menu_pid = menu_pid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getIco_view() {
        return ico_view;
    }

    public void setIco_view(String ico_view) {
        this.ico_view = ico_view;
    }

    public String getPer_action() {
        return per_action;
    }

    public void setPer_action(String per_action) {
        this.per_action = per_action;
    }

    public String getPer_controller() {
        return per_controller;
    }

    public void setPer_controller(String per_controller) {
        this.per_controller = per_controller;
    }

    public List<RmsMenu> getMenuArray() {
        return menuArray;
    }

    public void setMenuArray(List<RmsMenu> menuArray) {
        this.menuArray = menuArray;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RmsMenu)) return false;

        RmsMenu rmsMenu = (RmsMenu) o;

        return menu_id.equals(rmsMenu.menu_id);

    }

    @Override
    public int hashCode() {
        return menu_id.hashCode();
    }

    @Override
    public String toString() {
        return "RmsMenu{" +
                "menu_id=" + menu_id +
                ", menu_pid=" + menu_pid +
                ", remark='" + remark + '\'' +
                ", menu_name='" + menu_name + '\'' +
                ", per_action='" + per_action + '\'' +
                ", per_controller='" + per_controller + '\'' +
                ", ico_view='" + ico_view + '\'' +
                ", menuArray=" + menuArray +
                '}';
    }
}
