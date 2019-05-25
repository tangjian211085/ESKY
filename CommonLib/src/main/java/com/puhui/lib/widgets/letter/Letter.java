package com.puhui.lib.widgets.letter;

import java.io.Serializable;

/**
 * 版    权:  深圳市迪蒙网络科技有限公司
 * 描    述:  <描述>
 * Created by tangjian on 2016/9/12.
 */
public class Letter implements Serializable {
    private static final long serialVersionUID = -3066238414882401619L;
    private String letterName;

    private boolean isSelected;

    public Letter() {
    }

    public Letter(String letterName, boolean isSelected) {
        this.letterName = letterName;
        this.isSelected = isSelected;
    }

    public String getLetterName() {
        return letterName;
    }

    public void setLetterName(String letterName) {
        this.letterName = letterName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
