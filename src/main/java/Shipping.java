import com.algorithm.LargestAreaFitFirstPackager;
import com.packing.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shipping {
  List<BoxItem> productsAll = new ArrayList<BoxItem>();
  static Connection conn;

  public static void main(String[] args) {
        List<Dimension> containers = new ArrayList<Dimension>();
      HashMap<String, ArrayList<String>> boxPlacements = new HashMap<>();

//        containers.add(new Dimension("Jibby", 6, 6, 6));
//        containers.add(new Dimension("Tun", 6, 6, 6));

//      containers.add(new Dimension("Abang", 6, 6, 6));
//        System.out.println(containers.get(0).hashCode());
//        System.out.println(containers.get(1).hashCode());

      containers.add(new Dimension("Fed Ex", 1, 300, 300, 300));
    System.out.println("CONTAINERERRRR : "  + containers.get(0).getType());

        Packager packager = new LargestAreaFitFirstPackager(containers);

      Shipping shipping = new Shipping();
      List<BoxItem> products = shipping.dbConnect();
      System.out.println("product size: " + products.size());

//        List<BoxItem> products = new ArrayList<BoxItem>();
//        products.add(new BoxItem(new Box("Foot", 6, 10, 2), 1));
//        products.add(new BoxItem(new Box("Leg", 4, 6, 1), 1));
//        products.add(new BoxItem(new Box("Ass", 6, 6, 6), 1));
//        products.add(new BoxItem(new Box("Arm", 2, 3, 2), 1));
//        products.add(new BoxItem(new Box("Arm2", 1, 1, 1), 1));
//        products.add(new BoxItem(new Box("Arm3", 1, 2, 1), 1));


//        products.add(new BoxItem(new Box("Arm", 5, 5, 3), 1));

        // match to container
//        ArrayList<Container> match = packager.pack(products);
      ArrayList<Container> match = packager.pack(products);

        for(Container container : match) {
            System.out.println("new container height: " + container.getBoxCount() + " -- " + match.size());
            for(Level level : container.getLevels()) {
                System.out.println("containers: " + level.toString() + " ---- " + container.getName() + " -- " + level.size());
                for(Placement placement : level) {
                  placement.getBox().fittedTruck = container.getName();
                  placement.getBoxPlacementInfo().keySet().removeAll(boxPlacements.keySet());
                  boxPlacements.putAll(placement.getBoxPlacementInfo());
                  System.out.println("hehehehehehehe: " + container.getType() + container.numberOfPackages++);
//                  for(Map.Entry<String, String> entry : boxPlacements.entrySet()) {
//                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                  }
                }

//              System.out.println("placement: " + level.get(0);
            }
        }

        for(Map.Entry<String, ArrayList<String>> entry : boxPlacements.entrySet()) {
          System.out.println("Key = " + entry.getKey() + ", coordinate = " + entry.getValue().get(0) + " trucktype = " + entry.getValue().get(1) + " dimentsion = " + entry.getValue().get(2));
        }

        for(Dimension dimension : match) {
          System.out.println("FINALLY : " + dimension.getDbUpdateData() + " -- " + dimension.numberOfPackages);
        }

        insertTruckSummary();

        String sql = "UPDATE dbo.orderlist SET coordinate = ?, TruckType = ?, Dimension = ? WHERE caseno = ?";
    try {
      PreparedStatement preparedStatement = conn.prepareStatement(sql);

      for(Map.Entry<String, ArrayList<String>> entry : boxPlacements.entrySet()) {
        System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        String insertStatement = "UPDATE dbo.orderlist SET coordinate = '" + entry.getValue() + "' WHERE caseno = '" + entry.getKey() + "'";
        preparedStatement.setString(1, entry.getValue().get(0));
        preparedStatement.setString(2, entry.getValue().get(1));
        preparedStatement.setString(3, entry.getValue().get(2));
        preparedStatement.setString(4, entry.getKey());
        System.out.println(sql);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
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

  private static void insertTruckSummary() {
    String sql2 = "INSERT INTO dbo.trucksummary (trucktype, truckno, optimizationpercentage, coordinate, dimensionx, dimensiony, dimensionz, numberofpackages) VALUES "
        + ()
    PreparedStatement preparedStatement = conn.prepareStatement(sql);



  }

  public List<BoxItem> dbConnect() {




    try {
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      conn = DriverManager.getConnection("jdbc:sqlserver://rm-zf8xs7c9uo9xkv0ytwo.mssql.kualalumpur.rds.aliyuncs.com:1433;database=Planner;",
          "superadmin", "P@ssw0rd");
      System.out.println("connected");
      Statement statement = conn.createStatement();
      String queryString = "select top 10 * from dbo.orderlist";
      ResultSet rs = statement.executeQuery(queryString);
        while (rs.next()) {
//          System.out.println(rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getString(6));
          productsAll.add(new BoxItem(new Box(rs.getString(3), Integer.parseInt(rs.getString(4)), Integer.parseInt(rs.getString(5)), Integer.parseInt(rs.getString(6)))));
        }
      System.out.println(productsAll.size());
        return productsAll;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return productsAll;
  }
}