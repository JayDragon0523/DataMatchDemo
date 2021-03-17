package com.test;

import com.test.domain.Trace;
import com.test.domain.Weibo;
import com.test.service.CompareMethod;
import com.test.service.DataCompress;
import com.test.utils.LocationUtil;
import java.util.Collections;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        DataCompress dataCompress = new DataCompress();
        //1.读入数据初始化
        dataCompress.readData();
        List<Weibo> weiboList = dataCompress.weiboList;
        List<Trace> traceList = dataCompress.traceList.subList(0,200);
        List<String> errorMatch = new ArrayList<>();
        File file = new File("src\\main\\java\\com\\test\\weiboLocation.txt");
//        writeWeiboLocation(weiboList,file);
//        readWeiboLocation(weiboList,file);
        System.out.println(traceList.get(20).getTraceLng());
        //离群点排除
        List<Trace> newList = dataCompress.DataPreprocessing(traceList);
        traceList = newList;
        weiboList = dataCompress.WbDataPreprocessing(weiboList);
//        for(int i=0;i<traceList.get(0).getTraceLng().size();i++){
//            System.out.println(traceList.get(0).getTraceLng().get(i)+","+traceList.get(0).getTraceLat().get(i));
//        }
//        for(int i=0;i<24;i++){
//            System.out.println(traceList.get(0).getTraceLng().get(i)+","+traceList.get(0).getTraceLat().get(i));
//        }


        //数据量统计
        List<Integer> cntData = dataAnalysis(weiboList,traceList);


//        2.信令数据降维
        //1.均匀采样
        List<Trace> us_result = dataCompress.UniformSampling(traceList,cntData.get(7));
        //2.分段聚合
        List<Trace> ccxl_result = dataCompress.CoarseCompress(us_result);
        List<Weibo> ccwb_result = dataCompress.CoarseCompressWB(weiboList);

        //数据量统计
        dataAnalysis(weiboList,us_result);
//
//
//        //3.轨迹匹配
        CompareMethod cnt = new CompareMethod();
        cnt.dataCompress = dataCompress;

        //聚类粗筛
        /*for(int i = 1 ; i <= traceList.size(); i++){
            cnt.StructCluster(i+"",ccxl_result,ccwb_result);
        }*/


        double ac = 0;
        for(int i = 1;i<= traceList.size();i++) {

            String path = "src\\main\\java\\com\\test\\cluster\\text"+traceList.get(i-1).getId()+".text";
            List<Weibo> to_Comapre = readCluster(path,weiboList);
//            List<Weibo> to_Comapre = weiboList;

            if(to_Comapre.size() == 0){
                to_Comapre = weiboList;
            }
            /*System.out.println("待匹配数量："+to_Comapre.size());
            for(Weibo w:to_Comapre){
                System.out.println(w.getId());
            }*/

            //压缩
//            List<Trace> newList = dataCompress.compressByWeiboData(weiboList.get(i-1),traceList);
            //1.欧氏距离
//            Map<String, Double> results = cnt.EuclideanDistance(traceList.get(i-1),to_Comapre);
//            Map<String, Double> results = cnt.EuclideanDistance_AC(i+"",traceList);
            //2.Hausdorff距离
//            Map<String, Double> results = cnt.HausdorffDistance(traceList.get(i-1),to_Comapre);
//            Map<String, Double> results = cnt.HausdorffDistance_AC(i+"",newList);
            //3.DTW距离
            Map<String, Double> results = cnt.DTW(us_result.get(i-1),to_Comapre);
            //这里将map.entrySet()转换成list
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(results.entrySet());
            //然后通过比较器来实现排序
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                //升序排序
                public int compare(Map.Entry<String, Double> o1,
                                   Map.Entry<String, Double> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }

            });

            int top = 5;
            System.out.println("信令轨迹id："+i);
            boolean flag = false;
            for (Map.Entry<String, Double> mapping : list) {
                if(top == 5 && mapping.getKey().equals(i+"")){
                    ac++;
                }
                /*if(mapping.getKey().equals(i+"")){
                    ac++;
                    flag = true;
                    break;
                }*/
                else if(top == 5 && !mapping.getKey().equals(i+"")){
                    errorMatch.add(i+"");
                }
                if(top-->0){
                    System.out.println(mapping.getKey() + ":" + mapping.getValue());
//                        System.out.println(weiboList.get(Integer.parseInt(mapping.getKey())-1).getTraceLng().toString());
//                        System.out.println(weiboList.get(Integer.parseInt(mapping.getKey())-1).getTraceLat().toString());
                }
                else{
//                    if(!flag)
//                        errorMatch.add(i+"");
                    break;
                }
            }
        }
        double p,r,f1;
        p = ac / traceList.size();
        r = ac / traceList.size();//样本中有多少正样本被预测正确了
        if(p+r == 0){
            f1 = 0;
        }
        else {
            f1 = p*r*2/(p+r);
        }
        System.out.println("准确率："+p);
//        System.out.println("召回率："+r);
//        System.out.println("f1-score："+f1);
        System.out.println(errorMatch);
        double minTraceSum = cnt.minTraceNum.stream().reduce(Integer::sum).orElse(0);
        double noiseNumSum = cnt.noiseNum.stream().reduce(Integer::sum).orElse(0);
        double clsNumSum = cnt.clsNum.stream().reduce(Integer::sum).orElse(0);
        System.out.println("聚类数目："+clsNumSum/cnt.clsNum.size());System.out.println("簇内最小轨迹数："+minTraceSum/cnt.minTraceNum.size());System.out.println("噪声轨迹数："+noiseNumSum/cnt.noiseNum.size());

    }

    public static List<Weibo> readCluster(String path,List<Weibo> weibos) throws IOException {
        File file = new File(path);
        StringBuilder result = new StringBuilder();
        List<Weibo> weiboList = new ArrayList<>();
        List<String> weibo_id = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                if(s.equals("cluster 0:")){
                    s = br.readLine();
                    if(!isInteger(s) && s.substring(0,4).equals("imsi")){
                        while((s = br.readLine())!=null && isInteger(s)){
                            weibo_id.add(s);
                        }
                    }
                    else {
                        return weiboList;
                    }
                }
            }

            /*//文件头复位
            br.mark((int)file.length()+1);
            br.reset();*/
            br.close();

            for (int i = 0; i < weibo_id.size(); i++) {
                for (int j = 0; j < weibos.size(); j++) {
                    if (weibo_id.get(i).equals(weibos.get(j).getId())){
                        weiboList.add(weibos.get(j));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return weiboList;
    }

    /**
    * @description: 微博信令数据简单统计
    * @param: [weiboList, traceList]
    * @return: void
    * @author: JayDragon
    * @date: 2021/1/13
    */
    public static List<Integer> dataAnalysis(List<Weibo> weiboList, List<Trace> traceList){
        
        int max_trace = 0, min_trace = 100005, max_weibo = 0, min_weibo = 100005;
        int avg_trace = 0, avg_weibo = 0;
        for (int i=0;i<weiboList.size();i++){
            int id = i+1;
            if(i < traceList.size()){
                if(max_trace < traceList.get(i).getTraceLat().size()){
                    max_trace = traceList.get(i).getTraceLat().size();
                }
                if(min_trace > traceList.get(i).getTraceLat().size()){
                    min_trace = traceList.get(i).getTraceLat().size();
                }
                avg_trace += traceList.get(i).getTraceLat().size();
            }

            if(max_weibo < weiboList.get(i).getTracetime().size()){
                max_weibo = weiboList.get(i).getTracetime().size();
            }
            if(min_weibo > weiboList.get(i).getTracetime().size()){
                min_weibo = weiboList.get(i).getTracetime().size();
            }
            avg_weibo += weiboList.get(i).getTracetime().size();
//            System.out.println("id:"+id + "     信令轨迹大小："+traceList.get(i).getTraceLat().size()+"     微博数据大小："+weiboList.get(i).getTracetime().size());
        }
        System.out.println("信令总数据量大小："+traceList.size());
        System.out.println("微博总数据量大小："+weiboList.size());
        System.out.println("信令轨迹数目最大值："+max_trace + "      信令轨迹数目最小值："+min_trace+ "      信令轨迹数目平均值："+avg_trace/traceList.size());
        System.out.println("微博轨迹数目最大值："+max_weibo + "      微博轨迹数目最小值："+min_weibo+ "      微博轨迹数目平均值："+avg_weibo/weiboList.size());
        List<Integer> result = new ArrayList<>();
        result.add(traceList.size());result.add(weiboList.size());
        result.add(max_trace);result.add(min_trace);result.add(avg_trace/weiboList.size());
        result.add(max_weibo);result.add(min_weibo);result.add(avg_weibo/weiboList.size());
        return  result;
    }
    
    /**
    * @description: 调用接口获取微博地址的经纬度并写入文件
    * @param: [weiboList]
    * @return: void
    * @author: JayDragon
    * @date: 2021/1/5
    */
    public static void writeWeiboLocation(List<Weibo> weiboList,File file) throws IOException {

        List<Double> traceLng = new ArrayList<>();
        List<Double> traceLat = new ArrayList<>();

        for (Weibo value : weiboList) {
            List<String> addressList = value.getLocation();
            for (String s : addressList) {
                Double[] loc = LocationUtil.getCoordinate(s);
                traceLng.add(loc[0]);
                traceLat.add(loc[1]);
            }
        }

        if(!file.exists() && !file.isDirectory()){
            file.createNewFile();
        }
        Writer out = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(out);
        for(int i = 0;i<traceLat.size();i++){
            String str= traceLng.get(i)+","+traceLat.get(i);
            bw.write(str);
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }


    /**
    * @description: 从文件读取微博经纬度数据
    * @param: [file]
    * @return: void
    * @author: JayDragon
    * @date: 2021/1/6
    */
    public static void readWeiboLocation(List<Weibo> weiboList,File file){

        List<Double> traceLng = new ArrayList<>();
        List<Double> traceLat = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行

                String[] arr = s.split(",");
                traceLng.add(Double.parseDouble(arr[0]));
                traceLat.add(Double.parseDouble(arr[1]));
            }
            br.close();
            int from = 0;
            int to = 0;
            for (int i = 0;i < weiboList.size(); i++){
                to += weiboList.get(i).getTracetime().size();
                weiboList.get(i).setTraceLng(traceLng.subList(from,to));
                weiboList.get(i).setTraceLat(traceLat.subList(from,to));
                from = to;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
