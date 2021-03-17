import com.test.Main;
import com.test.domain.Trace;
import com.test.service.CompareMethod;
import com.test.service.DataCompress;
import com.test.utils.LocationUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: DataMatchDemo
 * @description: 测试类
 * @author: JayDragon
 * @create: 2020-12-29 17:05
 **/
public class Test {
    public static void main(String[] args) {

        /*List<String> address = new ArrayList<>();
        address.add("浙江省杭州市拱墅区小河路711-12");
        address.add("浙江省杭州市西湖区白堤");
        address.add("浙江省杭州市拱墅区环城北路309号206室");
        address.add("浙江省杭州市下城区东新路33号");
        for (String s : address){
            Double[] loc = LocationUtil.getCoordinate(s);
            System.out.println(loc[0]);
            System.out.println(loc[1]);
        }*/
        testDataProcess();

//        List<Double> x = new ArrayList<>(){ 1,1,1,10,2,3 };
//        Double[] y = { 1,1,1,2,10,3 };
//        Double[] z = { 2, 5, 7, 7, 7, 7, 2 };
//        System.out.println(cm.getDistance(x, y));
//        System.out.println(cm.getDistance(x, z));
    }

    public static void testDataProcess(){
        File file = new File("src\\main\\java\\com\\test\\weiboLocation.txt");
        CompareMethod cm = new CompareMethod();

        DataCompress dataCompress = new DataCompress();

        dataCompress.readData();
        Main.readWeiboLocation(dataCompress.weiboList,file);

        List<Double> disList = new ArrayList<>();

        System.out.println(dataCompress.traceList.get(20).getTraceLng());
        System.out.println(dataCompress.traceList.get(20).getTraceLat());
        for(int i = 0 ;i < dataCompress.traceList.get(20).getTraceLng().size()-1;i++){
            Double lat1 = dataCompress.traceList.get(20).getTraceLat().get(i);
            Double lng1 = dataCompress.traceList.get(20).getTraceLng().get(i);
            Double lat2 = dataCompress.traceList.get(20).getTraceLat().get(i+1);
            Double lng2 = dataCompress.traceList.get(20).getTraceLng().get(i+1);
            Double dis = DataCompress.distanceOfTwoPoints(lat1,lng1,lat2,lng2);
            disList.add(dis);
        };
        List<Trace> traces = new ArrayList<>();
        traces.add(dataCompress.traceList.get(20));
        traces = dataCompress.DataPreprocessing(traces);
        //        Collections.sort(disList);
        System.out.println(disList);
    }

}
