package com.test.service;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.test.domain.Trace;
import com.test.domain.Weibo;
import org.apache.commons.collections.map.MultiKeyMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: DataMatchDemo
 * @description: 进行匹配的方法
 * @author: JayDragon
 * @create: 2020-12-29 20:19
 **/
public class CompareMethod {

    public DataCompress dataCompress;
    public List<Integer> clsNum = new ArrayList<>();
    public List<Integer> minTraceNum = new ArrayList<>();
    public List<Integer> noiseNum = new ArrayList<>();




    public Map<String,Double> EuclideanDistance(Trace trace,List<Weibo> weiboList) throws FileNotFoundException{
        long a=System.currentTimeMillis();
        Map<String, Double> results = new HashMap<String,Double>();

        //信令数据
        List<Double> lnga = trace.getTraceLng();
        List<Double> lata = trace.getTraceLat();

        int vi=0;

        for(int k = 0;k<weiboList.size();k++) {
            //微博数据
            List<Double> lngb = weiboList.get(k).getTraceLng();
            List<Double> latb = weiboList.get(k).getTraceLat();
            double distance = 0.0;
            int sum = 0;
            int size = Math.min(lnga.size(),lngb.size());
            for (int i = 0; i < size; i++) {
//                if (lnga.get(i) != 0 && lngb.get(i) != 0) {
                double d = Math.sqrt(Math.pow((lnga.get(i) - lngb.get(i)), 2) + Math.pow((lata.get(i) - latb.get(i)), 2));
                distance = distance + d;
                sum++;
//                }
            }
            distance = distance / sum;
            results.put(k+1+"",distance);
        }
        System.out.println("\r执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
        return results;
    }

    /**
     * @description: 欧几里得距离
     * @param: [collName, imsi]
     * @return: void
     * @author: JayDragon
     * @date: 2020/12/29
     */
    public Map<String,Double> EuclideanDistance_AC(String id,List<Trace> traces) throws FileNotFoundException{
        long a=System.currentTimeMillis();
        Map<String, Double> results = new HashMap<String,Double>();


        int imsi = Integer.parseInt(id)-1;
        //微博数据
        List<Double> lnga = dataCompress.weiboList.get(imsi).getTraceLng();
        List<Double> lata = dataCompress.weiboList.get(imsi).getTraceLat();

		/*for(int i=0; i<lnga.size(); i++){
			System.out.println(lnga.get(i)+" "+lata.get(i));
	    }*/

        int vi=0;

        for(int k = 0;k<traces.size();k++) {
            //信令数据
            List<Double> lngb = traces.get(k).getTraceLng();
            List<Double> latb = traces.get(k).getTraceLat();
            double distance = 0.0;
            int sum = 0;
//            int size = Math.min(lnga.size(),lngb.size());
            for (int i = 0; i < lnga.size(); i++) {
//                if (lnga.get(i) != 0 && lngb.get(i) != 0) {
                    double d = Math.sqrt(Math.pow((lnga.get(i) - lngb.get(i)), 2) + Math.pow((lata.get(i) - latb.get(i)), 2));
                    distance = distance + d;
                    sum++;
//                }
            }
            distance = distance / sum;
            results.put(k+1+"",distance);
        }
        System.out.println("\r执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
        return results;
    }

    /**
    * @description: Hausdorff距离计算
    * @param: [id]
    * @return: java.lang.Double
    * @author: JayDragon
    * @date: 2020/12/29
    */
    public Map<String,Double> HausdorffDistance(Trace trace,List<Weibo> weiboList) {

        long a=System.currentTimeMillis();
        //信令数据
        List<Double> lnga = trace.getTraceLng();
        List<Double> lata = trace.getTraceLat();

        Map<String, Double> results = new HashMap<>();

        for(int k = 0;k<weiboList.size();k++){
            //微博数据
            List<Double> lngb = weiboList.get(k).getTraceLng();
            List<Double> latb = weiboList.get(k).getTraceLat();

            List<Double> hab = new ArrayList<Double>();
            for(int i=0; i<lnga.size(); i++){
                if(lnga.get(i)!=0.0){
                    List<Double> disab = new ArrayList<Double>();
                    for(int j=0; j<lngb.size(); j++){
                        if(lngb.get(j)!=0.0){
                            disab.add(Math.sqrt(Math.pow(Math.abs(lnga.get(i)-lngb.get(j)), 2)+Math.pow(Math.abs(lata.get(i)-latb.get(j)), 2)));
                        }
                    }
                    hab.add(pickMin(disab));
                }
            }
            double h1 = pickMax(hab);

            List<Double> hba = new ArrayList<Double>();
            for(int i=0; i<lngb.size(); i++){
                if(lngb.get(i)!=0.0){
                    List<Double> disba = new ArrayList<Double>();
                    for(int j=0; j<lnga.size(); j++){
                        if(lnga.get(j)!=0.0){
                            disba.add(Math.sqrt(Math.pow(Math.abs(lnga.get(j)-lngb.get(i)), 2)+Math.pow(Math.abs(lata.get(j)-latb.get(i)), 2)));
                        }
                    }
                    hba.add(pickMin(disba));
                }
            }
            double h2 = pickMax(hba);

            double h=0.0;
            h = Math.max(h1, h2);
            results.put(k+1+"",h);
        }


        System.out.println("\rHausdorff距离计算执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

        return results;
    }

    public Map<String,Double> HausdorffDistance_AC(String id,List<Trace> traces) {

        long a=System.currentTimeMillis();
        int imsi = Integer.parseInt(id)-1;
        //微博数据
        List<Double> lnga = dataCompress.weiboList.get(imsi).getTraceLng();
        List<Double> lata = dataCompress.weiboList.get(imsi).getTraceLat();

        Map<String, Double> results = new HashMap<>();

        for(int k = 0;k<traces.size();k++){
            //信令数据
            List<Double> lngb = traces.get(k).getTraceLng();
            List<Double> latb = traces.get(k).getTraceLat();

            if(lngb.get(0)==0.0){
                results.put(k+1+"",Double.MAX_VALUE);
                continue;
            }

            List<Double> hab = new ArrayList<Double>();
            for(int i=0; i<lnga.size(); i++){
                if(lnga.get(i)!=0.0){
                    List<Double> disab = new ArrayList<Double>();
                    for(int j=0; j<lngb.size(); j++){
                        if(lngb.get(j)!=0.0){
                            disab.add(Math.sqrt(Math.pow(Math.abs(lnga.get(i)-lngb.get(j)), 2)+Math.pow(Math.abs(lata.get(i)-latb.get(j)), 2)));
                        }
                    }
                    hab.add(pickMin(disab));
                }
            }
            double h1 = pickMax(hab);

            List<Double> hba = new ArrayList<Double>();
            for(int i=0; i<lngb.size(); i++){
                if(lngb.get(i)!=0.0){
                    List<Double> disba = new ArrayList<Double>();
                    for(int j=0; j<lnga.size(); j++){
                        if(lnga.get(j)!=0.0){
                            disba.add(Math.sqrt(Math.pow(Math.abs(lnga.get(j)-lngb.get(i)), 2)+Math.pow(Math.abs(lata.get(j)-latb.get(i)), 2)));
                        }
                    }
                    hba.add(pickMin(disba));
                }
            }
            double h2 = pickMax(hba);

            double h=0.0;
            h = Math.max(h1, h2);
            results.put(k+1+"",h);
        }


        System.out.println("\rHausdorff距离计算执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

        return results;
    }

    /**
    * @description: DTW算法
    * @param: []
    * @return: java.util.Map<java.lang.String,java.lang.Double>
    * @author: JayDragon
    * @date: 2021/2/2
    */
    public Map<String,Double> DTW(Trace trace,List<Weibo> weiboList){

        //信令数据
        List<Double> lnga = trace.getTraceLng();
        List<Double> lata = trace.getTraceLat();

        Map<String, Double> results = new HashMap<>();

        for (Weibo w:weiboList){
            //微博数据
            List<Double> lngb = w.getTraceLng();
            List<Double> latb = w.getTraceLat();
            //经度
            double h1 = getDistance(lnga, lngb);
            //纬度
            double h2 = getDistance(lata, latb);
            double h = h1+h2;
            results.put(w.getId(),h);
        }
        return results;
    }

    //DTW辅助方法
    public double getDistance(List<Double> seqa, List<Double> seqb) {
        double distance = 0;
        int lena = seqa.size();
        int lenb = seqb.size();
        double[][] c = new double[lena][lenb];
        for (int i = 0; i < lena; i++) {
            for (int j = 0; j < lenb; j++) {
                c[i][j] = 1;
            }
        }
        for (int i = 0; i < lena; i++) {
            for (int j = 0; j < lenb; j++) {
                double tmp = (seqa.get(i) - seqb.get(j)) * (seqa.get(i) - seqb.get(j));
                if (j == 0 && i == 0)
                    c[i][j] = tmp;
                else if (j > 0)
                    c[i][j] = c[i][j - 1] + tmp;
                if (i > 0) {
                    if (j == 0)
                        c[i][j] = tmp + c[i - 1][j];
                    else
                        c[i][j] = tmp + getMin(c[i][j - 1], c[i - 1][j - 1], c[i - 1][j]);
                }
            }
        }
        distance = c[lena - 1][lenb - 1];
        return distance;
    }


    int minpts = 50;
    double radius = 0.04;
    List<List<Object[]>> clusters;
    List<Object[]> cores;
    public void StructCluster(String imsi, List<Trace> traces, List<Weibo> weibos) throws FileNotFoundException{
        String path = "src\\main\\java\\com\\test\\cluster\\text"+imsi+".text";
        FileOutputStream fs = new FileOutputStream(new File(path));
//        FileOutputStream fs = new FileOutputStream(new File("C:\\programyj\\MyEclipse\\Workspaces\\MyEclipse 10\\text.txt"));
        PrintStream p = new PrintStream(fs);

        Object[][] traceset = new Object[301][43];

        HashMap<String, Object> imsif = new HashMap<String,Object>();
        Object[] trace1 = new Object[43];
        //信令数据
        List<Double> lng1 = traces.get(Integer.parseInt(imsi)-1).getTraceLng();
        List<Double> lat1 = traces.get(Integer.parseInt(imsi)-1).getTraceLat();
        trace1[0] = "imsi:"+traces.get(Integer.parseInt(imsi)-1).getId().toString();
        for(int i=0; i<lng1.size(); i++){
            trace1[2*i+1] = lng1.get(i);
            trace1[2*i+2] = lat1.get(i);
        }
        traceset[0]=trace1;

        int vi=1;

        //微博数据
        for(int k = 0;k<weibos.size();k++){
            Object[] trace = new Object[43];
            List<Double> lng = weibos.get(k).getTraceLng();
            List<Double> lat = weibos.get(k).getTraceLat();
            trace[0] = weibos.get(k).getId();
            for(int i=0; i<lng.size(); i++){
                trace[2*i+1] = lng.get(i);
                trace[2*i+2] = lat.get(i);
            }
            traceset[vi]=trace;
            if(vi==9999){
                break;
            }
            vi++;
        }

        cores = findCores(traceset, minpts, radius);
		
		/*for(Object[] core:cores){  
		    p.println("["+core[0]+"]");    
		}*/
        p.println(cores.size()+" core points:");

        //putCoreToCluster();
        clusters = new ArrayList<List<Object[]>>();
        int clusterNum = 0;
        for(int i = 0;i<cores.size();i++){
            clusters.add(new ArrayList<Object[]>());
            clusters.get(clusterNum).add(cores.get(i));
            densityConnected(traceset, cores.get(i), clusterNum);
            clusterNum++;
        }

        int i = 0;
        clsNum.add(new Integer(clusterNum));
        int minClus = 999999;
        for(List<Object[]> cluster:clusters){
            p.println("cluster "+ i++ +":");
            if (cluster.size() < minClus){
                minClus = cluster.size();
            }
            for(Object[] point:cluster){
                p.println(point[0]);
            }
        }
        minTraceNum.add(new Integer(minClus));
        int flag = 0;
        int numNoise = 0;
        for(int j = 0;j<traceset.length;j++){
            flag = 0;
            for(List<Object[]> cluster:clusters){
                if(cluster.contains(traceset[j])){
                    flag = 1;
                    break;
                }
            }
            if(flag==0){
                p.println("noise point:"+traceset[j][0]);
                numNoise++;
            }
        }
        noiseNum.add(new Integer(numNoise));
        p.close();

    }

    /**
    * @description: 寻找核心点，即待测点半径范围内点数量>minpts
    * @param: [points, minpts, radius]
    * @return: java.util.List<java.lang.Object[]>
    * @author: JayDragon
    * @date: 2021/2/2
    */
    public List<Object[]> findCores(Object[][] points,int minpts,double radius){
        List<Object[]> cores = new ArrayList<Object[]>();
        for(int i = 0; i < points.length;i++){
            int pts = 0;
            for(int j = 0; j < points.length;j++){
                for(int k = 0; k < points[i].length;k++){
                    if(countEurDistance(points[i], points[j])<radius){
                        pts++;
                    }
                }
            }
            if(pts>=minpts){
                cores.add(points[i]);
            }
        }
        return cores;
    }

    public double countEurDistance(Object[] point1,Object[] point2){
        double eurDistance = 0.0;
        int sum = 0;
        int size = Math.min(point1.length,point2.length);
        for(int i=1;i<size;i+=2){
            if((Double)point1[i]!=0.0 && (Double)point2[i]!=0.0){
                eurDistance += Math.sqrt(((Double)point1[i]-(Double)point2[i])*((Double)point1[i]-(Double)point2[i])+((Double)point1[i+1]-(Double)point2[i+1])*((Double)point1[i+1]-(Double)point2[i+1]));
                sum++;
            }
        }
        return (Double)eurDistance/sum;
    }

    public void densityConnected(Object[][] points,Object[] core,int clusterNum){
        boolean isputToCluster;//是否已经归为某个类
        boolean isneighbour = false;//是不是core的“邻居”
        cores.remove(core);//对某个core点处理后就从core集中去掉
        for(int i = 0; i < points.length;i++){
            isneighbour = false;
            isputToCluster = false;
            for(List<Object[]> cluster:clusters){
                if(cluster.contains(points[i])){//如果已经归为某个类
                    isputToCluster = true;
                    break;
                }
            }
            if(isputToCluster)continue;//已在聚类中，跳过，不处理
            if(countEurDistance(points[i], core)<radius){//是目前加入的core点的“邻居”吗？，ture的话，就和这个core加入一个类
                clusters.get(clusterNum).add(points[i]);
                isneighbour = true;
            }
            if(isneighbour){//如果是邻居，才会接下来对邻居进行densityConnected处理，否则，结束这个core点的处理
                if(cores.contains(points[i])){
                    cores.remove(points[i]);
                    densityConnected(points, points[i], clusterNum);
                }
            }
        }
    }

    public double getMin(double a, double b, double c) {
        double min = a;
        if (b > a)
            min = a;
        else if (c > b) {
            min = b;
        } else {
            min = c;
        }
        return min;
    }

    public double pickMin(List<Double> dis){
        double min = dis.get(0);
        for (Double di : dis) {
            if (min >= di) {
                min = di;
            }
        }
        return min;
    }

    public double pickMax(List<Double> h){
        double max = h.get(0);
        for (Double aDouble : h) {
            if (max <= aDouble) {
                max = aDouble;
            }
        }
        return max;
    }
}
