package com.bhesky.app.bean;

import com.bhesky.app.utils.sqlite.annotation.Entity;
import com.bhesky.app.utils.sqlite.annotation.Property;

@SuppressWarnings("unused")
@Entity("table_user")
public class User {
    @Property(columnName = "id")
    private String userId;

    private String realName;

    private int age;

    private String idCardNum;

    private int height;

//    @Property(columnName = "password", oldColumnName = "pwd")
    @Property(columnName = "pwd")
    private String password;

    private String tradePassword; //交易密码

    @Property(columnName = "remain")
    private double remainAmount; //剩余财产

    private double totalAmount; //总财产

    private double investAmount; //投资财产

    private String emailAddress;

    private String qqNumber;

    private String wechatAccount;

    private String nickName;

    private String school;

    public byte[] photo;  //头像

    private String homeAddress; //家庭地址

    private String workAddress;  //工作地址

    //    private String[] bankCards;  //银行卡号  这种类型怎么存呢？？？？
//    @Transient   //设置不用存入数据库？？？
//    private List<BankCardInfo> mBankCardInfos;  //另外定义一个BankCardInfo类
    private String bankCardNum; //银行卡号

    private boolean isVip;

    /**
     * 使用DaoFactory需要一个无参的构造方法
     */
    public User() {
    }

    public User(String userId, String realName, int age, String idCardNum, int height, String password, String tradePassword,
                double remainAmount, double totalAmount, double investAmount, String emailAddress, String qqNumber,
                String wechatAccount, String nickName, String school, String homeAddress, String wordAddress, String bankCardNum, boolean isVip) {
        this.userId = userId;
        this.realName = realName;
        this.age = age;
        this.idCardNum = idCardNum;
        this.height = height;
        this.password = password;
        this.tradePassword = tradePassword;
        this.remainAmount = remainAmount;
        this.totalAmount = totalAmount;
        this.investAmount = investAmount;
        this.emailAddress = emailAddress;
        this.qqNumber = qqNumber;
        this.wechatAccount = wechatAccount;
        this.nickName = nickName;
        this.school = school;
        this.homeAddress = homeAddress;
        this.workAddress = wordAddress;
        this.bankCardNum = bankCardNum;
        this.isVip = isVip;
    }

//    public User(String userId, String realName, int age, String idCardNum, int height,
//                String password, String tradePassword, double remainAmount, double totalAmount,
//                double investAmount, String emailAddress, String qqNumber, String wechatAccount,
//                String nickName, String school, byte[] photo, String homeAddress, String workAddress, String bankCardNum, boolean isVip) {
//        this.userId = userId;
//        this.realName = realName;
//        this.age = age;
//        this.idCardNum = idCardNum;
//        this.height = height;
//        this.password = password;
//        this.tradePassword = tradePassword;
//        this.remainAmount = remainAmount;
//        this.totalAmount = totalAmount;
//        this.investAmount = investAmount;
//        this.emailAddress = emailAddress;
//        this.qqNumber = qqNumber;
//        this.wechatAccount = wechatAccount;
//        this.nickName = nickName;
//        this.school = school;
//        this.photo = photo;
//        this.homeAddress = homeAddress;
//        this.workAddress = workAddress;
//        this.bankCardNum = bankCardNum;
//        this.isVip = isVip;
//    }

    public User(String realName, int age, String idCardNum, String password) {
        this.realName = realName;
        this.age = age;
        this.idCardNum = idCardNum;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTradePassword() {
        return tradePassword;
    }

    public void setTradePassword(String tradePassword) {
        this.tradePassword = tradePassword;
    }

    public double getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(double remainAmount) {
        this.remainAmount = remainAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(double investAmount) {
        this.investAmount = investAmount;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

//    public byte[] getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(byte[] photo) {
//        this.photo = photo;
//    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getBankCardNum() {
        return bankCardNum;
    }

    public void setBankCardNum(String bankCardNum) {
        this.bankCardNum = bankCardNum;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", realName='" + realName + '\'' +
                ", age=" + age +
                ", idCardNum='" + idCardNum + '\'' +
                ", password='" + password + '\'' +
                ", tradePassword='" + tradePassword + '\'' +
                '}';
    }
}
