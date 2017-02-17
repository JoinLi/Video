package com.ly.video.bean;

/**
 * Created by MarioStudio on 2016/7/18.
 */
public class ConstantApi {

    public static final String Movie_Sou_Path = "http://api.hongshishui.cn/dianying2/search.php?keywords=";
    public static final String Movie_Number_Path ="http://api.hongshishui.cn/dianying2/";
    //播放地址
    public static final String Play_Path ="http://dy.beijingtianfu.com/";
    //爱奇艺
    public static final String Qy_Path ="http://dy.beijingtianfu.com/qy/";
    //爱奇艺电影
    public static final String Movie_Qy_Path =Qy_Path+"qyviplist.php?remen=&fenlei=&page=";
    //爱奇艺电视剧
    public static final String Tv_Qy_Path =Qy_Path+"qylist.php?page=";

    //搜狐
    public static final String Sh_Path ="http://dy.beijingtianfu.com/souhu/";
    //搜狐电影
    public static final String Movie_Sh_Path =Sh_Path+"shviplist.php?page=";
    //搜狐电视剧
    public static final String Tv_Sh_Path =Sh_Path+"list.php?page=";
    //图片地址
    public static final String Img_Path ="http://www.iketv.cn/images.php?url=http:";

    //乐视
    public static final String Ls_Path ="http://dy.beijingtianfu.com/le/";
    //乐视电影
    public static final String Movie_Ls_Path =Ls_Path+"leviplist.php?fenlei=&page=";
    //乐视tv
    public static final String Tv_Ls_Path =Ls_Path+"lelist.php";

    //芒果
    public static final String Mg_Path ="http://dy.beijingtianfu.com/mg/";
    //芒果电影
    public static final String Movie_Mg_Path =Mg_Path+"mgviplist.php?remen=&fenlei=&page=";
    //芒果tv
    public static final String Tv_Mg_Path =Mg_Path+"mglist.php?remen=&fenlei=&page=";

    //其他电影
    public static final String Movie_Other_Path = Movie_Number_Path+"list.php?page=";
    //其他电视剧
    public static final String Tv_Other_Path =Movie_Number_Path+ "mjlist.php?remen=3&fenlei=&page=";
    //版本更新
    public static final String UpdateUrl ="https://raw.githubusercontent.com/JoinLi/index/master/update.txt";



}
