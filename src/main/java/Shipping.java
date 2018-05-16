import com.algorithm.LargestAreaFitFirstPackager;
import com.packing.*;

import java.util.ArrayList;
import java.util.List;

public class Shipping {

    public static void main(String[] args) {
        List<Dimension> containers = new ArrayList<Dimension>();
        containers.add(new Dimension("Jibby", 6, 6, 6));
        containers.add(new Dimension("Tun", 6, 6, 6));
//        System.out.println(containers.get(0).hashCode());
//        System.out.println(containers.get(1).hashCode());


        Packager packager = new LargestAreaFitFirstPackager(containers);

        List<BoxItem> products = new ArrayList<BoxItem>();
        products.add(new BoxItem(new Box("Foot", 6, 10, 2), 1));
        products.add(new BoxItem(new Box("Leg", 4, 6, 1), 1));
        products.add(new BoxItem(new Box("Ass", 6, 6, 6), 1));
        products.add(new BoxItem(new Box("Arm", 2, 3, 2), 1));
//        products.add(new BoxItem(new Box("Arm", 5, 5, 3), 1));

        // match to container
        ArrayList<Container> match = packager.pack(products);

        for(Container container : match) {
            System.out.println("new container height: " + container.getBoxCount());
            for(Level level : container.getLevels()) {
                System.out.println("containers: " + level.toString());
            }
        }

//        System.out.println("Container height: " + match.getBoxCount());
//        System.out.println("container name: " + match.getName());

//        System.out.println("container levels: " + match.getLevels().get(0).get(1) + " -- " + match.getName());
//        System.out.println("Container: " + match.getLevels().get(0).get(0).toString()  + " -- " + match.getName());
//        System.out.println("Container: " + match.getLevels().get(0).get(1).toString());
//        System.out.println("Container: " + match.getLevels().get(1).get(0).toString());

//        for(Level level : match.getLevels()) {
//            System.out.println("containers: " + level.toString());
//        }

    }
}