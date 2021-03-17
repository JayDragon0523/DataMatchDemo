package com.test.service;

import com.test.domain.Trace;
import com.test.domain.Weibo;
import com.test.utils.ExcelUtil;
import org.apache.poi.ss.formula.functions.T;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: DataMatchDemo
 * @description: 数据降维
 * @author: JayDragon
 * @create: 2020-12-29 14:40
 **/
public class DataCompress {

    public List<Weibo> weiboList = new ArrayList<>();
    public List<Trace> traceList = new ArrayList<>();


    /**
    * @description: 读取excel数据
    * @param: []
    * @return: void
    * @author: JayDragon
    * @date: 2020/12/29
    */
    public void readData(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(new File(".").getAbsolutePath());
        ExcelUtil sheet1 = new ExcelUtil("src\\main\\java\\com\\test\\weibo_data_210309_noise.xls", "sheet 1");
        int rows = sheet1.getSheet().getPhysicalNumberOfRows();
        for(int i = 0; i<rows; i++){
            /*
            信令数据读取
             */
            String cell = sheet1.getExcelDateByIndex(i, 0);
            if(cell == null) continue; //判断空
            String[] temp;
            temp=cell.split(",");
            List<Double> traceLng = new ArrayList<>();
            List<Double> traceLat = new ArrayList<>();
            List<String> tracetime = new ArrayList<>();
            try {
                for (int j = 0; j < temp.length; j += 3) {
                    Double lng = Double.parseDouble(temp[j + 2].substring(2, 12));
                    Double lat = Double.parseDouble(temp[j + 1].substring(3, 12));
                    //重复数据处理
                    if(tracetime.contains(temp[j].substring(3, 22))) continue;
                    //缺失值处理
                    if(temp[j].equals("") || temp[j]==null){
                        continue;
                    }
                    //无效数据处理
                    Date date = sdf.parse(temp[j].substring(3, 22));
                    Date minDate = sdf.parse("2015-02-03 00:00:00");
                    Date maxDate = sdf.parse("2015-02-09 23:59:59");
                    if(date.before(minDate) || date.after(maxDate)){
                        continue;
                    }
                    tracetime.add(temp[j].substring(3, 22));
                    traceLng.add(lng);//经度
                    traceLat.add(lat);//纬度

                }
                traceList.add(new Trace(i + 1 + "", traceLng, traceLat, tracetime));
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("i:"+i);
            }

            /*
            微博数据读取
             */
            readWbData(sdf,sheet1,i);

            /*String cell2 = sheet1.getExcelDateByIndex(i, 1);
            temp = new String[105];
            List<String> location = new ArrayList<>();
            List<String> time = new ArrayList<>();
//            String pattern = "(.*?)\\([1-9]\\d*\\.?\\d*\\)";(\([^\)]+\))
            Pattern pattern = Pattern.compile("(\\([^\\)]+\\))");
            Matcher matcher = pattern.matcher(cell2);
            while (matcher.find()) {
//                System.out.println(matcher.group(0));
                temp=matcher.group(0).split(",");
//                System.out.println(temp[0]);
//                System.out.println(temp[1]);
                time.add(temp[0].substring(2,21));
                location.add(temp[1].substring(2,temp[1].length()-2));
//                System.out.println(temploc);
            }
            weiboList.add(new Weibo(i+1+"", time, location));*/

        }
        /*for(Weibo web : weiboList){
            System.out.println(web);
        }*/
    }

    public void readWbData(SimpleDateFormat sdf,ExcelUtil sheet1,int i){
        String cell2 = sheet1.getExcelDateByIndex(i, 1);
        int rows = sheet1.getSheet().getPhysicalNumberOfRows();
        String cell = sheet1.getExcelDateByIndex(i, 1);
        String[] temp;
        temp=cell.split(",");
        List<Double> traceLng = new ArrayList<>();
        List<Double> traceLat = new ArrayList<>();
        List<String> tracetime = new ArrayList<>();
        try {
            for (int j = 0; j < temp.length; j += 3) {
                Double lng = Double.parseDouble(temp[j + 2].substring(2, 10));
                Double lat = Double.parseDouble(temp[j + 1].substring(2, 11));
                //重复数据处理
                if(tracetime.contains(temp[j].substring(3, 22))) continue;
                //缺失值处理
                if(temp[j].equals("") || temp[j]==null){
                    continue;
                }
                //无效数据处理
                Date date = sdf.parse(temp[j].substring(3, 22));
                Date minDate = sdf.parse("2015-02-03 00:00:00");
                Date maxDate = sdf.parse("2015-02-09 23:59:59");
                if(date.before(minDate) || date.after(maxDate)){
                    continue;
                }
                tracetime.add(temp[j].substring(3, 22));
                traceLng.add(lng);//经度
                traceLat.add(lat);//纬度

            }
            weiboList.add(new Weibo(i + 1 + "", traceLng, traceLat, tracetime));

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("i:"+i);
        }
    }

    /**
    * @description: 根据两点间经纬度坐标（double值），计算两点间距离
    * @param: [lat1, lng1, lat2, lng2]
    * @return: double
    * @author: JayDragon
    * @date: 2021/2/4
    */
    public static double distanceOfTwoPoints(double lat1,double lng1,
                                             double lat2,double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static final double EARTH_RADIUS = 6378137;

    private static double minDistance = 0;
    private static double maxDistance = 25000;

    /**
    * @description: 信令轨迹离群点排除
    * @param: [traceList]
    * @return: java.util.List<com.test.domain.Trace>
    * @author: JayDragon
    * @date: 2021/2/4
    */
    public List<Trace> DataPreprocessing(List<Trace> traceList){
        List<Trace> result = new ArrayList<>();
        boolean flag = false;
        if (traceList.size() != 0) {
            for(int i = 0; i < traceList.size(); i++) {
                List<Double> traceLng = new ArrayList<>();
                List<Double> traceLat = new ArrayList<>();
                List<String> tracetime = new ArrayList<>();
                for (int j = 0; j < traceList.get(i).getTraceLng().size(); j++) {
                    Double lng = traceList.get(i).getTraceLng().get(j);
                    Double lat = traceList.get(i).getTraceLat().get(j);
                    String time = traceList.get(i).getTracetime().get(j);
                    //经纬度杭州范围判断 杭州范围 经度 118.21-120.30  纬度 29.11-30.33
                    if (lng > 118.21 && lng < 120.30 && lat > 29.11 && lat < 30.33) {
                        //进行存储
                        if (traceLat.isEmpty()) {
                            //第一个点，直接添加
                            traceLng.add(lng);
                            traceLat.add(lat);
                            tracetime.add(time);
                        } else if (traceLat.size() == 1) {
                            //第二个点，要计算距离
                            traceLng.add(lng);
                            traceLat.add(lat);
                            tracetime.add(time);
                            //--计算距离
                            double distance = distanceOfTwoPoints(traceLat.get(0), traceLng.get(0), lat, lng);
                            //判断距离，如果符合，则直接加入，并开始绘制；如果不符合，计算第三个点
                            if (!(distance >= minDistance && distance < maxDistance)) {
                                //不符合速度要求,需要采集第三个点，判断是第一个不符合还是第二个不符合
                                flag = true;
                            }
                        } else if (traceLat.size() == 2) {
                            //判断第三个点是不是需要计算
                            double distance = distanceOfTwoPoints(traceLat.get(1), traceLng.get(1), lat, lng);
                            if (flag) {
                                traceLng.add(lng);
                                traceLat.add(lat);
                                tracetime.add(time);
                                //说明前两个点不符合要求，要根据第三个判断前两个到底是哪个不对
                                if (distance >= minDistance && distance < maxDistance) {
                                    //第二个和第三个点距离合适，说明是第一个点不对的，去掉第一个点
                                    traceLng.remove(0);
                                    traceLat.remove(0);
                                    tracetime.remove(0);
                                    flag = false;
                                } else {
                                    //第二个和第三个距离也不对，说明是第二个不对
                                    traceLng.remove(1);
                                    traceLat.remove(1);
                                    tracetime.remove(1);
                                    flag = false;
                                }
                            } else {
                                //说明前两个点是符合要求的，第三个不管了，直接判断后上传
                                if (distance >= minDistance && distance < maxDistance) {
                                    traceLng.add(lng);
                                    traceLat.add(lat);
                                    tracetime.add(time);
                                }
                            }
                        } else {
                            //三个点都完了之后的了，直接判断就行了
                            double distance = distanceOfTwoPoints(traceLat.get(traceLat.size()-1), traceLng.get(traceLat.size() - 1), lat, lng);
//                            System.out.println(distance);
//                            System.out.println("lat1:"+traceLat.get(traceLat.size()-1)+" lng1:"+traceLng.get(traceLat.size() - 1)+ " lat2:"+lat+" lng2:"+ lng);
                            if (distance >= minDistance && distance < maxDistance) {
                                traceLng.add(lng);
                                traceLat.add(lat);
                                tracetime.add(time);
                            }
                        }
                    }
                }
                result.add(new Trace(traceList.get(i).getId(), traceLng, traceLat, tracetime));
            }
        }
        return result;
    }


    /**
    * @description: 微博轨迹离群点排除
    * @param: [weiboList]
    * @return: java.util.List<com.test.domain.Weibo>
    * @author: JayDragon
    * @date: 2021/3/16
    */
    public List<Weibo> WbDataPreprocessing(List<Weibo> weiboList){
        List<Weibo> result = new ArrayList<>();
        boolean flag = false;
        if (weiboList.size() != 0) {
            for(int i = 0; i < weiboList.size(); i++) {
                List<Double> traceLng = new ArrayList<>();
                List<Double> traceLat = new ArrayList<>();
                List<String> tracetime = new ArrayList<>();
                for (int j = 0; j < weiboList.get(i).getTraceLng().size(); j++) {
                    Double lng = weiboList.get(i).getTraceLng().get(j);
                    Double lat = weiboList.get(i).getTraceLat().get(j);
                    String time = weiboList.get(i).getTracetime().get(j);
                    //经纬度杭州范围判断 杭州范围 经度 118.21-120.30  纬度 29.11-30.33
                    if (lng > 118.21 && lng < 120.30 && lat > 29.11 && lat < 30.33) {
                        //进行存储
                        if (traceLat.isEmpty()) {
                            //第一个点，直接添加
                            traceLng.add(lng);
                            traceLat.add(lat);
                            tracetime.add(time);
                        } else if (traceLat.size() == 1) {
                            //第二个点，要计算距离
                            traceLng.add(lng);
                            traceLat.add(lat);
                            tracetime.add(time);
                            //--计算距离
                            double distance = distanceOfTwoPoints(traceLat.get(0), traceLng.get(0), lat, lng);
                            //判断距离，如果符合，则直接加入，并开始绘制；如果不符合，计算第三个点
                            if (!(distance >= minDistance && distance < maxDistance)) {
                                //不符合速度要求,需要采集第三个点，判断是第一个不符合还是第二个不符合
                                flag = true;
                            }
                        } else if (traceLat.size() == 2) {
                            //判断第三个点是不是需要计算
                            double distance = distanceOfTwoPoints(traceLat.get(1), traceLng.get(1), lat, lng);
                            if (flag) {
                                traceLng.add(lng);
                                traceLat.add(lat);
                                tracetime.add(time);
                                //说明前两个点不符合要求，要根据第三个判断前两个到底是哪个不对
                                if (distance >= minDistance && distance < maxDistance) {
                                    //第二个和第三个点距离合适，说明是第一个点不对的，去掉第一个点
                                    traceLng.remove(0);
                                    traceLat.remove(0);
                                    tracetime.remove(0);
                                    flag = false;
                                } else {
                                    //第二个和第三个距离也不对，说明是第二个不对
                                    traceLng.remove(1);
                                    traceLat.remove(1);
                                    tracetime.remove(1);
                                    flag = false;
                                }
                            } else {
                                //说明前两个点是符合要求的，第三个不管了，直接判断后上传
                                if (distance >= minDistance && distance < maxDistance) {
                                    traceLng.add(lng);
                                    traceLat.add(lat);
                                    tracetime.add(time);
                                }
                            }
                        } else {
                            //三个点都完了之后的了，直接判断就行了
                            double distance = distanceOfTwoPoints(traceLat.get(traceLat.size()-1), traceLng.get(traceLat.size() - 1), lat, lng);
//                            System.out.println(distance);
//                            System.out.println("lat1:"+traceLat.get(traceLat.size()-1)+" lng1:"+traceLng.get(traceLat.size() - 1)+ " lat2:"+lat+" lng2:"+ lng);
                            if (distance >= minDistance && distance < maxDistance) {
                                traceLng.add(lng);
                                traceLat.add(lat);
                                tracetime.add(time);
                            }
                        }
                    }
                }
                result.add(new Weibo(weiboList.get(i).getId(), traceLng, traceLat, tracetime));
            }
        }
        return result;
    }


    /**
    * @description: 粗粒度压缩,直接按每天8个小时划分，分时间段进行聚合
    * @param: [collName]
    * @return: void
    * @author: JayDragon
    * @date: 2020/12/30
    */
    public List<Trace> CoarseCompress(List<Trace> traceList){
        List<Trace> result = new ArrayList<>();
        try {
            int vi = 0;
            String[] timeseg1 = {"00","01","02","03","04","05","06","07"};
            String[] timeseg2 = {"08","09","10","11","12","13","14","15"};
            String[] timeseg3 = {"16","17","18","19","20","21","22","23"};
            List<List<String>> timeSeg = new ArrayList<>();
            timeSeg.add(Arrays.asList(timeseg1));timeSeg.add(Arrays.asList(timeseg2));timeSeg.add(Arrays.asList(timeseg3));
            String[] dataseg = {"2015-02-03","2015-02-04","2015-02-05","2015-02-06","2015-02-07","2015-02-08","2015-02-09"};

            for (Trace trace : traceList) {
                double[] lngnum = new double[21];
                double[] latnum = new double[21];
                int[] num = new int[21];
                String[] timenum = new String[21];
//                double[] anglenum = new double[24];
                int roll = 0;
                int day = 0;
                List<Double> lngList = trace.getTraceLng();
                List<Double> latList = trace.getTraceLat();
                List<String> timeList = trace.getTracetime();
                //System.out.println(temp.get("IMSI"));
                for (int i = 0; i < timeList.size(); i++) {
                    String str = timeList.get(i).substring(11, 13);
                    if (timeSeg.get(roll%3).contains(str)) {
                        lngnum[roll] = lngnum[roll] + lngList.get(i);
                        latnum[roll] = latnum[roll] + latList.get(i);
                        num[roll] = num[roll] + 1;//轨迹段数量++
                    } else {
                        while (!timeSeg.get(roll%3).contains(str)) {
                            roll++;
                        }
                        lngnum[roll] = lngnum[roll] + lngList.get(i);
                        latnum[roll] = latnum[roll] + latList.get(i);
                        num[roll] = num[roll] + 1;
                    }
                }

                DecimalFormat df = new DecimalFormat("#.000000");
                for(int j=0; j<21; j++){
                    if(num[j]==0){
                        lngnum[j] = 0.0;
                        latnum[j] = 0.0;
                    }else{
                        lngnum[j] = Double.parseDouble(df.format(lngnum[j]/num[j]));
                        latnum[j] = Double.parseDouble(df.format(latnum[j]/num[j]));
                    }
                    //System.out.println(lngnum[j]+" "+latnum[j]);
                }

                for (int i = 0; i < 21; i++) {
                    timenum[i] = dataseg[i/3] + " " + timeSeg.get(i%3).get(3) + ":30:00.0000";
                    //System.out.println(lngnum[i]+" "+latnum[i]+" "+timenum[i]);
                }
                Trace temp = new Trace();
                temp.setId(trace.getId());
                temp.setTraceLng(doubleToList(lngnum));
                temp.setTraceLat(doubleToList(latnum));
                temp.setTracetime(Arrays.asList(timenum));
                result.add(temp);
                /*if(vi==0){
				    break;
			    }
			    vi++;*/

            }


        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<Weibo> CoarseCompressWB(List<Weibo> traceList){
        List<Weibo> result = new ArrayList<>();
        try {
            int vi = 0;
            String[] timeseg1 = {"00","01","02","03","04","05","06","07"};
            String[] timeseg2 = {"08","09","10","11","12","13","14","15"};
            String[] timeseg3 = {"16","17","18","19","20","21","22","23"};
            List<List<String>> timeSeg = new ArrayList<>();
            timeSeg.add(Arrays.asList(timeseg1));timeSeg.add(Arrays.asList(timeseg2));timeSeg.add(Arrays.asList(timeseg3));
            String[] dataseg = {"2015-02-03","2015-02-04","2015-02-05","2015-02-06","2015-02-07","2015-02-08","2015-02-09"};

            for (Weibo trace : traceList) {
                double[] lngnum = new double[21];
                double[] latnum = new double[21];
                int[] num = new int[21];
                String[] timenum = new String[21];
//                double[] anglenum = new double[24];
                int roll = 0;
                int day = 0;
                List<Double> lngList = trace.getTraceLng();
                List<Double> latList = trace.getTraceLat();
                List<String> timeList = trace.getTracetime();
                //System.out.println(temp.get("IMSI"));
                for (int i = 0; i < timeList.size(); i++) {
                    String str = timeList.get(i).substring(11, 13);
                    if (timeSeg.get(roll%3).contains(str)) {
                        lngnum[roll] = lngnum[roll] + lngList.get(i);
                        latnum[roll] = latnum[roll] + latList.get(i);
                        num[roll] = num[roll] + 1;//轨迹段数量++
                    } else {
                        while (!timeSeg.get(roll%3).contains(str)) {
                            roll++;
                        }
                        lngnum[roll] = lngnum[roll] + lngList.get(i);
                        latnum[roll] = latnum[roll] + latList.get(i);
                        num[roll] = num[roll] + 1;
                    }
                }

                DecimalFormat df = new DecimalFormat("#.000000");
                for(int j=0; j<21; j++){
                    if(num[j]==0){
                        lngnum[j] = 0.0;
                        latnum[j] = 0.0;
                    }else{
                        lngnum[j] = Double.parseDouble(df.format(lngnum[j]/num[j]));
                        latnum[j] = Double.parseDouble(df.format(latnum[j]/num[j]));
                    }
                    //System.out.println(lngnum[j]+" "+latnum[j]);
                }

                for (int i = 0; i < 21; i++) {
                    timenum[i] = dataseg[i/3] + " " + timeSeg.get(i%3).get(3) + ":30:00.0000";
                    //System.out.println(lngnum[i]+" "+latnum[i]+" "+timenum[i]);
                }
                Weibo temp = new Weibo();
                temp.setId(trace.getId());
                temp.setTraceLng(doubleToList(lngnum));
                temp.setTraceLat(doubleToList(latnum));
                temp.setTracetime(Arrays.asList(timenum));
                result.add(temp);

                /*if(vi==0){
				    break;
			    }
			    vi++;*/

            }


        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @Description: 细粒度降维
     * @Param: [collName]
     * @return: void
     * @Author: JayDragon
     * @Date: 2020/12/28
     */
    public List<Trace> FineCompress(List<Trace> traceList){
        List<Trace> result = new ArrayList<>();
        try {
            int vi=0;
            String[] timeseg1 = {"00","01","02","03","04","05","06","07","08","09","10","11",
                    "12","13","14","15","16","17","18","19","20","21","22","23"};
            for (Trace trace : traceList) {
                int roll = 0;
                List<Double> lngList = trace.getTraceLng();
                List<Double> latList = trace.getTraceLat();
                List<String> timeList = trace.getTracetime();


                double lngtemp = lngList.get(0);
                double lattemp = latList.get(0);
                String timetemp = timeList.get(0);
                List<Double> minlngList = new ArrayList<Double>();
                List<Double> minlatList = new ArrayList<Double>();
                List<Double> lng = new ArrayList<Double>();
                List<Double> lat = new ArrayList<Double>();
                List<String> time = new ArrayList<String>();

                for(int i=1; i<timeList.size(); i++){
                    if(timeList.get(i).substring(11, 13).equals(timeseg1[roll])){
                        //移动是否超过1000m
                        if(Math.pow((lngList.get(i)-lngtemp)*111000, 2)+Math.pow((latList.get(i)-lattemp)*96120, 2)<1000000){
                            minlngList.add(lngList.get(i));
                            minlatList.add(latList.get(i));
                        }else{
                            //超过1000m将上一个子段进行聚合
                            double minlng = 0.0;
                            double minlat = 0.0;
                            for(int j=0; j<minlngList.size(); j++){
                                minlng = minlng + minlngList.get(j);
                                minlat = minlat + minlatList.get(j);
                            }
                            minlng = minlng / minlngList.size();
                            minlat = minlat / minlatList.size();
                            //System.out.println("test1 "+minlngList.size());
                            lng.add(minlng);
                            lat.add(minlat);
                            time.add(timeList.get(i-1));
                            lngtemp = lngList.get(i);
                            lattemp = latList.get(i);
                            timetemp = timeList.get(i);
                            minlngList.clear();
                            minlatList.clear();
                            minlngList.add(lngList.get(i));
                            minlatList.add(latList.get(i));
                        }
                    }else{
                        if(minlngList.size()==0){
                            lng.add(0.0);
                            lat.add(0.0);
                            time.add("2015-02-05 "+timeseg1[roll]+":30:00.0000");
                        }else{
                            //不在同一小时中
                            double minlng = 0.0;
                            double minlat = 0.0;
                            for(int j=0; j<minlngList.size(); j++){
                                minlng = minlng + minlngList.get(j);
                                minlat = minlat + minlatList.get(j);
                            }
                            minlng = minlng / minlngList.size();
                            minlat = minlat / minlatList.size();
                            //System.out.println("test2 "+minlngList.size());

                            lng.add(minlng);
                            lat.add(minlat);
                            time.add(timeList.get(i-1));
                            lngtemp = lngList.get(i);
                            lattemp = latList.get(i);
                            timetemp = timeList.get(i);
                            minlngList.clear();
                            minlatList.clear();
                        }

                        roll++;//下一时辰
                        while(!(timeList.get(i).substring(11, 13).equals(timeseg1[roll]))){
                            lng.add(0.0);
                            lat.add(0.0);
                            time.add("2015-02-05 "+timeseg1[roll]+":30:00.0000");
                            roll++;
                        }
                        lngtemp = lngList.get(i);
                        lattemp = latList.get(i);
                        timetemp = timeList.get(i);
                        minlngList.add(lngList.get(i));
                        minlatList.add(latList.get(i));
                    }
                }


                //System.out.println(minlngList.size());
                //最后一子段的聚合
                double minlng = 0.0;
                double minlat = 0.0;
                for(int j=0; j<minlngList.size(); j++){
                    minlng = minlng + minlngList.get(j);
                    minlat = minlat + minlatList.get(j);
                }
                minlng = minlng / minlngList.size();
                minlat = minlat / minlatList.size();
                //System.out.println("test2 "+minlngList.size());

                lng.add(minlng);
                lat.add(minlat);
                time.add(timeList.get(timeList.size()-1));
				/*for(int k=0; k<lng.size(); k++){
					System.out.println(lng.get(k)+" "+lat.get(k)+" "+time.get(k));
				}*/

                Trace temp = new Trace();
                temp.setId(trace.getId());
                temp.setTraceLng(lng);
                temp.setTraceLat(lat);
                temp.setTracetime(time);
                result.add(temp);

				/*if(vi==1){
					break;
				}
				vi++;*/
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @description: 均匀采样
     * @param: [collName]
     * @return: void
     * @author: JayDragon
     * @date: 2020/12/29
     */
    public List<Trace> UniformSampling(List<Trace> traceList, Integer index){
        List<Trace> result = new ArrayList<>();
        int vi = 0;

        for (Trace trace : traceList) {
            List<Double> lngList = trace.getTraceLng();
            List<Double> latList = trace.getTraceLat();
            List<String> timeList = trace.getTracetime();

            List<Double> lngnum = new ArrayList<Double>();
            List<Double> latnum = new ArrayList<Double>();
            List<String> timenum = new ArrayList<String>();
            int k = timeList.size() / index.intValue();
            for(int i=0; i<timeList.size(); i+=k){
                lngnum.add(lngList.get(i));
                latnum.add(latList.get(i));
                timenum.add(timeList.get(i));
            }

			/*for(int j=0; j<lngnum.size(); j++){
				System.out.println(lng.get(j)+" "+lat.get(j)+" "+time.get(j));
			}*/
            Trace temp = new Trace();
            temp.setId(trace.getId());
            temp.setTraceLng(lngnum);
            temp.setTraceLat(latnum);
            temp.setTracetime(timenum);
            result.add(temp);
			/*if(vi==0){
				break;
			}
			vi++;*/
        }
        return result;
    }


    List<String> source_id = new ArrayList<String>();
    List<Double> source_lng = new ArrayList<Double>();
    List<Double> source_lat = new ArrayList<Double>();
    List<String> source_time = new ArrayList<String>();
    List<Double> result_lng = new ArrayList<Double>();
    List<Double> result_lat = new ArrayList<Double>();
    List<String> result_time = new ArrayList<String>();
    List<Integer> index = new ArrayList<Integer>();// 记录保留的节点在原始曲线节点坐标数组中的位置
    /**
     * @description: Douglas-Peucker压缩算法
     * @param: [collName]
     * @return: void
     * @author: JayDragon
     * @date: 2020/12/29
     */
    public List<Trace> DouglasPeucker(List<Trace> traceList){//

        List<Trace> result = new ArrayList<>();
        int vi=0;

        for(Trace trace: traceList) {

            String id = trace.getId();
            List<Double> lngList = trace.getTraceLng();
            List<Double> latList = trace.getTraceLat();
            List<String> timeList = trace.getTracetime();

            int sumtemp = 0;

            for (int i = 0; i < timeList.size(); i++) {
                source_lng.add(lngList.get(i));
                source_lat.add(latList.get(i));
                source_time.add(timeList.get(i));
            }

            index.add(0);
            index.add(source_lng.size() - 1);

            //递归
            compress(0, source_lng.size() - 1);
            sort();

            for (int i = 0; i < index.size(); i++) {
                result_lng.add(source_lng.get(index.get(i)));
                result_lat.add(source_lat.get(index.get(i)));
                result_time.add(source_time.get(index.get(i)));
                //System.out.println(result_lng.get(i)+" "+result_lat.get(i)+" "+result_time.get(i));
            }
            Trace temp = new Trace();

            temp.setId(trace.getId());
            temp.setTraceLng(result_lng);
            temp.setTraceLat(result_lat);
            temp.setTracetime(result_time);

            result.add(temp);

            source_lng.clear();
            source_lat.clear();
            source_time.clear();
            result_lng.clear();
            result_lat.clear();
            result_time.clear();
            index.clear();

        }
        return result;
    }

    /**
     * @description: 按照微博数据压缩
     * @param: [collName]
     * @return: void
     * @author: JayDragon
     * @date: 2020/12/29
     */
    public List<Trace> compressByWeiboData(Weibo weibo,List<Trace> traces){

        int vi = 0;
        List<Trace> newList = new ArrayList<>();
        for (Trace trace : traces) {
            int size = 0;
            List<Double> lngList = trace.getTraceLng();
            List<Double> latList = trace.getTraceLat();
            List<String> timeList = trace.getTracetime();

            List<Double> lngnum = new ArrayList<Double>();
            List<Double> latnum = new ArrayList<Double>();
            List<String> timenum = new ArrayList<String>();
            int index = 0;
            for (int i = 0; i < weibo.getTracetime().size(); i++) {
                for(int j=index; j<timeList.size(); j++){
                    if(timeList.get(j).substring(0,13).equals(weibo.getTracetime().get(i).substring(0,13))){
                        lngnum.add(lngList.get(index));
                        latnum.add(latList.get(index));
                        timenum.add(timeList.get(index));
                        size++;
                        break;
                    }
                    index++;
                }
            }

			/*for(int j=0; j<lngnum.size(); j++){
				System.out.println(lng.get(j)+" "+lat.get(j)+" "+time.get(j));
			}*/
            if(size < weibo.getTracetime().size()){
                for (int i = 0; i < weibo.getTracetime().size()-size; i++) {
                    lngnum.add(0.0);
                    latnum.add(0.0);
                }
            }
            Trace t = new Trace();
            t.setTraceLng(lngnum);
            t.setTraceLat(latnum);
            t.setTracetime(timenum);

            newList.add(t);


			/*if(vi==0){
				break;
			}
			vi++;*/
        }
        return newList;

    }

    public void compress(int i, int j) {
        double temp_dist;
        double max = 0;
        int temp_p = 0;

        for (int k = i + 1; k < j; k++) {
            temp_dist = distance(i, j, k);
            if (max < temp_dist) {
                max = temp_dist;
                temp_p = k;
            }
        }

        if (max > 0.01) {
            index.add(temp_p);
            compress(i, temp_p);
            compress(temp_p, j);
        }
    }

    public void sort() {
        for (int i = 0; i < index.size(); i++) {
            for (int j = i + 1; j < index.size(); j++) {
                if (index.get(j) < index.get(i)) {
                    int temp = index.get(j);
                    index.set(j, index.get(i));
                    index.set(i,temp);
                }
            }
        }
    }

    public double distance(int start, int end, int current) {

        double a = (double) (source_lat.get(end) - source_lat.get(start));
        double b = (double) (source_lng.get(end) - source_lng.get(start));
        double c = (double) (source_lat.get(end) - source_lat.get(start))
                - (double) (source_lng.get(end) - source_lng.get(start));

        double dist = Math.abs(a * source_lng.get(current) + b * source_lat.get(current)+ c) / Math.sqrt(a * a + b * b);
        return dist;
    }

    /**
    * @description: 实现数组元素的翻转
    * @param: [arr]
    * @return: java.lang.String[]
    * @author: JayDragon
    * @date: 2020/12/29
    */
    public static String[] reverse(String[] arr){
        // 遍历数组
        for(int i = 0;i < arr.length / 2;i++){
            // 交换元素
            String temp = arr[arr.length -i - 1];
            arr[arr.length -i - 1] = arr[i];
            arr[i] = temp;
        }
        // 返回反转后的结果
        return arr;
    }

    /**
    * @description: double[] 转List
    * @param: [arr_double]
    * @return: java.util.List<java.lang.Double>
    * @author: JayDragon
    * @date: 2021/1/5
    */
    public List<Double> doubleToList(double[] arr_double){
        List<Double> list=new ArrayList<Double>();
        int num=arr_double.length;
        Double [] arr_Double=new Double[num];
        for(int i=0;i<num;i++){
            arr_Double[i]=arr_double[i];//double[]转Double[]
        }
        list= Arrays.asList(arr_Double);//Double[]转List
        return list;
    }

}
