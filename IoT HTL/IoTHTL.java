import java.util.*;
import java.io.*;

public class IoTHTL {
    public static void main(String[] args) {
        Planning plan = new Planning();
        VCS VCS = new VCS();
        SensorFusion sf = new SensorFusion();
        Console input = System.console();
        String cmd;
        while (true){
            System.out.println("Current Speed: " + sf.getCurrentspeed());
            System.out.println("Current Position: ");
            try {
                FileInputStream coordData = new FileInputStream("./Data/carPosition");
                String displaycoords = "";
                int c;
                while((c = coordData.read()) != -1){
                    displaycoords += (char)c;
                }
                coordData.close();
                System.out.println(displaycoords);
            } catch (Exception e) {
            }
            try {
                FileInputStream buttonData = new FileInputStream("./Data/buttons");
                String displaydata = "";
                int c;
                while((c = buttonData.read()) != -1){
                    displaydata += (char)c;
                }
                buttonData.close();
                System.out.println(displaydata);
            } catch (Exception e) {
            }
            
            cmd = "";
            cmd = input.readLine("> ");
            String argarray[] = cmd.split(" ");
            plan.Plan(VCS, sf, argarray);
        }
    }
}
class VCS {
    private float[] getposanddir(){
        FileInputStream posdirsensor = null;
        String str = "";
        try {
            posdirsensor = new FileInputStream("./Data/carPosition");
            
            int character;
            while((character = posdirsensor.read()) != -1){
                str += (char)character;
            }
            posdirsensor.close();
        } catch (Exception e) {
            //shouldn't have exception if file is present and is named properly
        }
        float[] returnpos = new float[3];
        returnpos[0] = Float.parseFloat(str.substring(str.indexOf("X ") + new String("X ").length(), str.indexOf("Y")-1));
        returnpos[1] = Float.parseFloat(str.substring(str.indexOf("Y ") + new String("X ").length(), str.indexOf("T")-1));
        returnpos[2] = Float.parseFloat(str.substring(str.indexOf("T ") + 1));
        return returnpos;

    }
    private void setposanddir(float[] posanddir){
        PrintStream posdirsensor = null;
        try {
            posdirsensor = new PrintStream(new FileOutputStream("./Data/carPosition"));
            posdirsensor.println("X " + posanddir[0] + "\nY " + posanddir[1] + "\nT " + posanddir[2]);
            posdirsensor.close();
        } catch (Exception e) {
            //Shouldn't happen if working correctly
        }
        
    }
    void brake(){
        PrintStream speed = null;
        try {
            PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
            logs.println("Brakes Applied");
            logs.close();
            speed = new PrintStream(new FileOutputStream("./Data/speed"));
            speed.println("0");
            speed.close();
        } catch (Exception e) {
            //Shouldn't happen if working correctly
        }
    }
    void steer(int dir){
        float[] posanddir = getposanddir();
        
        if(dir == 1){
            //Turn left
            try {
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Left turn");
                logs.close();
            } catch (Exception e) {
            }
            if(posanddir[2] == 0){
                //Facing north
                posanddir[0] -= 1;
                posanddir[1] += 1;
                posanddir[2] = 1;
            }else if(posanddir[2] == 1){
                //Facing west
                posanddir[0] -= 1;
                posanddir[1] -= 1;
                posanddir[2] = 2;
            }else if (posanddir[2] == 2){
                //Facing south
                posanddir[0] += 1;
                posanddir[1] -= 1;
                posanddir[2] = 3;
            }else{
                //Facing East
                posanddir[0] += 1;
                posanddir[1] += 1;
                posanddir[2] = 0;
            }
        }else if(dir == -1){
            //Turn right
            try {
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Right turn");
                logs.close();
            } catch (Exception e) {
            }
            if(posanddir[2] == 0){
                //Facing north
                posanddir[0] += 1;
                posanddir[1] += 1;
                posanddir[2] = 3;
            }else if(posanddir[2] == 1){
                //Facing west
                posanddir[0] -= 1;
                posanddir[1] += 1;
                posanddir[2] = 0;
            }else if (posanddir[2] == 2){
                //Facing south
                posanddir[0] -= 1;
                posanddir[1] -= 1;
                posanddir[2] = 1;
            }else{
                //Facing East
                posanddir[0] += 1;
                posanddir[1] -= 1;
                posanddir[2] = 2;
            }
        }
        setposanddir(posanddir);
    }
    void accelerate(float speed){
        float posanddir[] = getposanddir();
        try {
            PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
            logs.append("Accelerating to " + speed + "mph\n");
            logs.close();
            PrintStream speedsensor = new PrintStream(new FileOutputStream("./Data/speed"));
            speedsensor.println(speed);
            speedsensor.close();
        } catch (Exception e) {
        }
        if (posanddir[2] == 0){
            //Facing North
            posanddir[1] += speed;
        }else if (posanddir[2] == 1){
            //Facing West
            posanddir[0] -= speed;
        }else if (posanddir[2] == 2){
            //Facing South
            posanddir[1] -= speed;
        }else{
            //Facing East
            posanddir[0] += speed;

        }
        setposanddir(posanddir);
    }
}

class SensorFusion {
    int NORTH = 0;
    int WEST = 1;
    int SOUTH = 2;
    int EAST = 3;
    int currentspeed = 0;
    Vector<String[]> objects = new Vector<String[]>();
    int streetSpeedLimit;
    float carPosition[] = new float[3];
    boolean sensorMonitoring = true;
    boolean steeroverride = false;
    boolean brakeoverride = false;
    boolean acceloverride = false;
    public void getposanddir(){
        FileInputStream posdirsensor = null;
        String str = "";
        try {
            posdirsensor = new FileInputStream("./Data/carPosition");
            
            int character;
            while((character = posdirsensor.read()) != -1){
                str += (char)character;
            }
            posdirsensor.close();
        } catch (Exception e) {
            //shouldn't have exception if file is present and is named properly
        }
        carPosition[0] = Float.parseFloat(str.substring(str.indexOf("X ") + new String("X ").length(), str.indexOf("Y")-1));
        carPosition[1] = Float.parseFloat(str.substring(str.indexOf("Y ") + new String("X ").length(), str.indexOf("T")-1));
        carPosition[2] = Float.parseFloat(str.substring(str.indexOf("T ") + 1));

    }
    public float getCurrentspeed() {
        FileInputStream speedSensor = null;
        String speedString = "";
        try {
            speedSensor = new FileInputStream("./Data/speed");
            int c;
            while((c =  speedSensor.read()) != -1){
                speedString += (char)c;
            }
            speedSensor.close();
        } catch (Exception e) {
            
        }
        float speed = Float.parseFloat(speedString);
        return speed;
    }
    public float getStreetSpeed() {
        String speedString = "";
        try {
            FileInputStream streetData = new FileInputStream("./Data/streetData");
            int c;
            while((c =  streetData.read()) != -1){
                speedString += (char)c;
            }
            streetData.close();
        } catch (Exception e) {
            
        }
        float streetSpeed = -1;
        String[] streets = speedString.split("\n");
        String carpos = "";
        try {
            FileInputStream posdirsensor = new FileInputStream("./Data/carPosition");
            
            int character;
            while((character = posdirsensor.read()) != -1){
                carpos += (char)character;
            }
            posdirsensor.close();
        } catch (Exception e) {
            //shouldn't have exception if file is present and is named properly
        }
        float carx = Float.parseFloat(carpos.substring(carpos.indexOf("X ") + new String("X ").length(), carpos.indexOf("Y")-1));
        float cary = Float.parseFloat(carpos.substring(carpos.indexOf("Y ") + new String("X ").length(), carpos.indexOf("T")-1));
        for(String street : streets){
            float speed;
            speed = Float.parseFloat(street.substring(street.indexOf("SpeedLimit ") + new String("SpeedLimit ").length(),street.indexOf("X") - 1));
            String xString = street.substring(street.indexOf("X ") + new String("X ").length(), street.indexOf("Y ") - 1);
            String yString = street.substring(street.indexOf("Y ") + new String("Y ").length());
            if(xString.contains("[")){
                float[] x = new float[2];
                x[0] = Float.parseFloat(xString.substring(xString.indexOf("[") + 1, xString.indexOf(",")));
                x[1] = Float.parseFloat(xString.substring(xString.indexOf(",") + 1, xString.indexOf("]")));
                float y = Float.parseFloat(yString);
                if(y == cary){
                    if(carx >= x[0] && carx <= x[1]){
                        streetSpeed = speed;
                        break;
                    }
                }
            }else{
                float [] y = new float[2];
                y[0] = Float.parseFloat(yString.substring(yString.indexOf("[") + 1, yString.indexOf(",")));
                y[1] = Float.parseFloat(yString.substring(yString.indexOf(",") + 1, yString.indexOf("]")));
                float x = Float.parseFloat(xString);
                if(x == carx){
                    if(cary >= y[0] && cary <= y[1]){
                        streetSpeed = speed;
                        break;
                    }
                }
            }
        }
        if (streetSpeed == -1){
            System.out.println("Offroad!");
            try {
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Car offroaded! Automatically shutting down.");
                logs.close();
            } catch (Exception e) {
            }
            System.exit(1);
        }
        return streetSpeed;
    }
    public Vector<String[]> getObstacleData() {
        String fullobstacleString = "";
        try{
            FileInputStream obstacleList = new FileInputStream("./Data/obstacleData");
            int c;
            while((c = obstacleList.read()) != -1){
                fullobstacleString += (char)c;
            }
            obstacleList.close();
        }catch(Exception e){

        }
        String[] obstacleList = fullobstacleString.split("\n");
        for(String obstacle : obstacleList){
            String name = obstacle.substring(0,obstacle.indexOf("X") - 1);
            String xcoord = obstacle.substring(obstacle.indexOf("X ") + 2,obstacle.indexOf("Y")-1);
            String ycoord = obstacle.substring(obstacle.indexOf("Y ") + 2);
            String[] obstaclearray = {name,xcoord,ycoord};
            objects.add(obstaclearray);
        }
        return objects;
    }
    public Vector<String[]> getRouteData(){
        Vector<String[]> RouteData = new Vector<String[]>();
        try {
            String fulltext = "";
            String[] arr;
            FileInputStream mapsAPI = new FileInputStream("./Data/gpsInstructions");
            int c;
            while((c = mapsAPI.read()) != -1){
                fulltext += (char)c;
            }
            arr = fulltext.split("END\n");
            for(int i = 0; i < arr.length; i++){
                RouteData.add(arr[i].split("\n"));
            }
            mapsAPI.close();
        } catch (Exception e) {
        }
        return RouteData;
    }

}

class Planning{
    CruiseControl cc = new CruiseControl();
    AdaptiveCruiseControl acc = new AdaptiveCruiseControl();
    AutomaticBraking ab = new AutomaticBraking();
    AutomaticSteering as = new AutomaticSteering();
    GPSNavigation gps = new GPSNavigation();
    void Plan(VCS VCS, SensorFusion sf,String argv[]) {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        sf.steeroverride = false;
        sf.acceloverride = false;
        sf.brakeoverride = false;
           //Get sensor fusion values
        sf.getposanddir();
        cc.requestCurrentSpeed(sf);
        acc.requestStreetSpeed(sf);
        ab.requestObstacleData(sf);
        as.requestObstacleData(sf);
        gps.requestRouteData(sf);
        if(argv[0].equals("c") && argv.length == 1){

        }
        else if(argv[0].equals("accelerate") && argv.length == 2){
            //accelerate (User override)
            sf.acceloverride = true;
            float speedinput = Float.parseFloat(argv[1]);
            boolean steer = as.planSteering(VCS,sf,speedinput);
            if(!steer){
                boolean brake = ab.planBraking(VCS,sf,speedinput);
                if(!brake){
                    VCS.accelerate(speedinput);
                }
            }
        }else if(argv[0].equals("steer") && argv.length == 2){
            //steer (User override)
            sf.steeroverride = true;
            if(argv[1].equals("left")){
                VCS.steer(1);
            }else if(argv[1].equals("right")){
                VCS.steer(-1);
            }
        }else if(argv[0].equals("brake") && argv.length == 1){
            //brake (user override)
            sf.brakeoverride = true;
            VCS.brake();
        }else if(argv[0].equals("cc") && argv.length == 1){
            if(acc.active){
                acc.display();
            }
            cc.display();
        }else if(argv[0].equals("acc") && argv.length == 1){
            if(cc.active){
                cc.display();
            }
            acc.display();
        }else if(argv[0].equals("ab") && argv.length == 1){
            ab.display();
        }else if(argv[0].equals("as") && argv.length == 1){
            as.display();
        }else if(argv[0].equals("gps") && argv.length > 1){
            gps.display();
            String pass = argv[1];
            for(int i = 2; i < argv.length; i++){
                pass = pass + " " + argv[i];
            }
            gps.planRoute(VCS, sf, pass);
            
        }else{
            System.out.println("Incorrect command or incorrect number of args");
        }

        if(cc.active == false && argv[0].equals("accelerate") == false){
            try {
                PrintStream speedSensor = new PrintStream(new FileOutputStream("./Data/speed"));
                speedSensor.println("0.0");
                speedSensor.close();
            } catch (Exception e) {
            }
        }
        sf.getposanddir();
        cc.requestCurrentSpeed(sf);
        acc.requestStreetSpeed(sf);
        ab.requestObstacleData(sf);
        as.requestObstacleData(sf);
        gps.requestRouteData(sf);
        cc.planSpeed(VCS,sf,ab,as);
        acc.planSpeed(VCS,sf,ab,as);
        gps.planRoute(VCS, sf, "");
    }
}

class CruiseControl{
    float speed;
    boolean acceloverride;
    boolean brakeoverride;
    boolean active = false;
    public void requestCurrentSpeed(SensorFusion sf){
        //Return sensor fusion value instead
        speed = sf.getCurrentspeed();
        acceloverride = sf.acceloverride;
        brakeoverride = sf.brakeoverride;
    }
    public void planSpeed(VCS vcs,SensorFusion sf, AutomaticBraking ab, AutomaticSteering as){
        if(acceloverride || brakeoverride){
            if(active){
                display();
            }
            
        }else{
            if(active){
                boolean autosteer = as.planSteering(vcs, sf, speed);
                if(!autosteer){
                    boolean autobrake = ab.planBraking(vcs, sf, speed);
                    if(!autobrake){
                    vcs.accelerate(speed);
                    }else{
                        display();
                    }
                }
            }
        }
    }
    public void display(){
        if(active == true){
            active = false;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("CC: true\nACC", "CC: false\nACC");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Cruise Control Deactivated");
                logs.close();

            } catch (Exception e) {
            }
        }else if (active == false){
            active = true;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("CC: false\nACC", "CC: true\nACC");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Cruise Control Activated");
                logs.close();

            } catch (Exception e) {
            }
        }
    }
}
class AdaptiveCruiseControl{
    float streetSpeed;
    boolean acceloverride;
    boolean brakeoverride;
    boolean active = false;
    public void requestStreetSpeed(SensorFusion sf){
        //Return sensor fusion value instead        
        streetSpeed = sf.getStreetSpeed();
        acceloverride = sf.acceloverride;
        brakeoverride = sf.brakeoverride;
    }
    public void planSpeed(VCS vcs,SensorFusion sf, AutomaticBraking ab, AutomaticSteering as){
        if(acceloverride || brakeoverride){
            if(active){
                display();
            }
            
        }else{
            if(active){
                boolean autosteer = as.planSteering(vcs, sf, streetSpeed);
                if(!autosteer){
                    boolean autobrake = ab.planBraking(vcs, sf, streetSpeed);
                    if(!autobrake){
                    vcs.accelerate(streetSpeed);
                    }else{
                        display();
                    }
                }
            }
        }
    }
    public void display(){
        if(active){
            active = false;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("ACC: true\n", "ACC: false\n");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Adaptive Cruise Control Deactivated");
                logs.close();

            } catch (Exception e) {
            }
        }else{
            active = true;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("ACC: false\n", "ACC: true\n");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Adaptive Cruise Control Activated");
                logs.close();

            } catch (Exception e) {
            }
        }
    }
}
class AutomaticBraking{
    Vector<String[]> obstacleData; 
    boolean brakeoverride;
    boolean active = false;
    public void requestObstacleData(SensorFusion sf){
        //get from sensor fusion
        obstacleData = sf.getObstacleData();
        brakeoverride = sf.brakeoverride;
    }
    public boolean planBraking(VCS vcs,SensorFusion sf, float speedin){
        if(brakeoverride){
            if(active){
                display();
            }
        }else{
            if(active){
                for(Integer i = 0; i < obstacleData.size(); i++){
                    String[] currObstacle= obstacleData.get(i);
                    float xcoord = Float.parseFloat(currObstacle[1]);
                    float ycoord = Float.parseFloat(currObstacle[2]);
                    float[] carpos = sf.carPosition;
                    if(carpos[2] == 0){
                        if(Math.abs(xcoord - carpos[0]) <= 0.5){
                            if(carpos[1] < ycoord  && (carpos[1] + speedin) >= ycoord){
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Automatic Braking triggered by object: " + currObstacle[0]);
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                vcs.accelerate(Math.abs(carpos[1] - ycoord) - 1);
                                vcs.brake();
                                display();
                                sf.brakeoverride = true;
                                return true;
                            }
                        }
                    }else if(carpos[2] == 1){
                        if(Math.abs(ycoord - carpos[1]) <= 0.5){
                            if(carpos[0] > xcoord  && (carpos[0] - speedin) <= xcoord){
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Automatic Braking triggered by object: " + currObstacle[0]);
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                vcs.accelerate(Math.abs(carpos[0] - xcoord) + 1);
                                vcs.brake();
                                display();
                                sf.brakeoverride = true;
                                return true;
                            }
                        }
                    }else if(carpos[2] == 2){
                        if(Math.abs(xcoord - carpos[0]) <= 0.5){
                            if(carpos[1] > ycoord  && (carpos[1] - speedin) <= ycoord){
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Automatic Braking triggered by object: " + currObstacle[0]);
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                vcs.accelerate(Math.abs(carpos[1] - ycoord) + 1);
                                vcs.brake();
                                display();
                                sf.brakeoverride = true;
                                return true;
                            }
                        }
                    }else{
                        if(Math.abs(ycoord - carpos[1]) <= 0.5){
                            if(carpos[0] < xcoord  && (carpos[0] + speedin) >= xcoord){
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Automatic Braking triggered by object: " + currObstacle[0]);
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                vcs.accelerate(Math.abs(xcoord - carpos[1]) - 1);
                                vcs.brake();
                                display();
                                sf.brakeoverride = true;
                                return true;
                            }
                        }
                    }
                }
            }
            else{
                for(Integer i = 0; i < obstacleData.size(); i++){
                    String[] currObstacle= obstacleData.get(i);
                    float xcoord = Float.parseFloat(currObstacle[1]);
                    float ycoord = Float.parseFloat(currObstacle[2]);
                    float[] carpos = sf.carPosition;
                    if(carpos[2] == 0){
                        if(Math.abs(xcoord - carpos[0]) <= 0.5){
                            if(carpos[1] < ycoord  && (carpos[1] + speedin) >= ycoord){
                                System.out.println("Crash!");
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Car crashed. Shutting down");
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                System.exit(1);
                            }
                        }
                    }else if(carpos[2] == 1){
                        if(Math.abs(ycoord - carpos[1]) <= 0.5){
                            if(carpos[0] > xcoord  && (carpos[0] - speedin) <= xcoord){
                                System.out.println("Crash!");
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Car crashed. Shutting down");
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                System.exit(1);
                            }
                            
                        }
                    }else if(carpos[2] == 2){
                        if(Math.abs(xcoord - carpos[0]) <= 0.5){
                            if(carpos[1] > ycoord  && (carpos[1] - speedin) <= ycoord){
                                System.out.println("Crash!");
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Car crashed. Shutting down");
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                System.exit(1);
                            }
                        }
                    }else{
                        if(Math.abs(ycoord - carpos[1]) <= 0.5){
                            if(carpos[0] < xcoord  && (carpos[0] + speedin) >= xcoord){
                                System.out.println("Crash!");
                                try {
                                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                    logs.println("Car crashed. Shutting down");
                                    logs.close();
                                    } catch (Exception e) {
                                    //Shouldn't happen if working correctly
                                    }
                                System.exit(1);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    public void display(){
        if(active){
            active = false;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("AB: true\n", "AB: false\n");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Automatic Braking Deactivated");
                logs.close();

            } catch (Exception e) {
            }
        }else{
            active = true;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("AB: false\n", "AB: true\n");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("Automatic Braking Activated");
                logs.close();

            } catch (Exception e) {
            }
        }
    }
}
   class AutomaticSteering{
       Vector<String[]> obstacleData; 
       boolean steeroverride;
       boolean active = false;
       public void requestObstacleData(SensorFusion sf){
           //get from sensor fusion
           obstacleData = sf.getObstacleData();
           steeroverride = sf.steeroverride;
       }
        public boolean planSteering(VCS vcs,SensorFusion sf, float speedin){
            if(steeroverride){
                if(active){
                    display();
                }
            }else{
                if(active){
                    for(Integer i = 0; i < obstacleData.size(); i++){
                        String[] currObstacle= obstacleData.get(i);
                        float xcoord = Float.parseFloat(currObstacle[1]);
                        float ycoord = Float.parseFloat(currObstacle[2]);
                        float[] carpos = sf.carPosition;
                        if(carpos[2] == 0){
                            if(Math.abs(xcoord - carpos[0]) <= 0.5){
                                if(carpos[1] < ycoord  && (carpos[1] + speedin) >= ycoord){
                                    System.out.println("Steered around object");
                                    try {
                                        PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                        logs.println("Automatic Steering triggered by object: " + currObstacle[0]);
                                        logs.close();
                                        } catch (Exception e) {
                                        //Shouldn't happen if working correctly
                                        }
                                    if(carpos[1] + speedin == ycoord){
                                        vcs.accelerate(speedin + 1);
                                    }else{
                                        vcs.accelerate(speedin);
                                    }
                                    return true;
                                }
                            }
                        }else if(carpos[2] == 1){
                            if(Math.abs(ycoord - carpos[1]) <= 0.5){
                                if(carpos[0] > xcoord  && (carpos[0] - speedin) <= xcoord){
                                    System.out.println("Steered around object");
                                    try {
                                        PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                        logs.println("Automatic Steering triggered by object: " + currObstacle[0]);
                                        logs.close();
                                        } catch (Exception e) {
                                        //Shouldn't happen if working correctly
                                        }
                                    if(carpos[0] - speedin == xcoord){
                                        vcs.accelerate(speedin + 1);
                                    }else{
                                        vcs.accelerate(speedin);
                                    }
                                    return true;
                                }
                            }
                        }else if(carpos[2] == 2){
                            if(Math.abs(xcoord - carpos[0]) <= 0.5){
                                if(carpos[1] > ycoord  && (carpos[1] - speedin) <= ycoord){
                                    System.out.println("Steered around object");
                                    try {
                                        PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                        logs.println("Automatic Steering triggered by object: " + currObstacle[0]);
                                        logs.close();
                                        } catch (Exception e) {
                                        //Shouldn't happen if working correctly
                                        }
                                    if(carpos[1] - speedin == ycoord){
                                        vcs.accelerate(speedin + 1);
                                    }else{
                                        vcs.accelerate(speedin);
                                    }
                                    return true;
                                }
                            }
                        }else{
                            if(Math.abs(ycoord - carpos[1]) <= 0.5){
                                if(carpos[0] < xcoord  && (carpos[0] + speedin) >= xcoord){
                                    System.out.println("Steered around object");
                                    try {
                                        PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                        logs.println("Automatic Steering triggered by object: " + currObstacle[0]);
                                        logs.close();
                                        } catch (Exception e) {
                                        //Shouldn't happen if working correctly
                                        }
                                    if(carpos[0] + speedin == xcoord){
                                        vcs.accelerate(speedin + 1);
                                    }else{
                                        vcs.accelerate(speedin);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
        public void display(){
            if(active){
                active = false;
                try {
                    String buttonString = "";
                    FileInputStream buttons = new FileInputStream("./Data/buttons");
                    
                    int c;
                    while ((c = buttons.read()) != -1){
                        buttonString += (char)c;
                    }
                    buttons.close();
                    String updatedString = buttonString.replace("AS: true\n", "AS: false\n");
                    PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                    buttonupdate.printf("%s",updatedString);
                    buttonupdate.close();
                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                    logs.println("Automatic Steering Deactivated");
                    logs.close();
    
                } catch (Exception e) {
                }
            }else{
                active = true;
                try {
                    String buttonString = "";
                    FileInputStream buttons = new FileInputStream("./Data/buttons");
                    
                    int c;
                    while ((c = buttons.read()) != -1){
                        buttonString += (char)c;
                    }
                    buttons.close();
                    String updatedString = buttonString.replace("AS: false\n", "AS: true\n");
                    PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                    buttonupdate.printf("%s",updatedString);
                    buttonupdate.close();
                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                    logs.println("Automatic Steering Activated");
                    logs.close();
    
                } catch (Exception e) {
                }
            }
        }
   }
   class GPSNavigation{
       Vector<String[]> routeData;
       String[] currentRoute;
       boolean acceloverride;
       boolean brakeoverride;
       boolean steeroverride;
       boolean active;
       int routeIndex;
       public void requestRouteData(SensorFusion sf){
           //get from sensor fusion
           routeData = sf.getRouteData();
           acceloverride = sf.acceloverride;
           brakeoverride = sf.brakeoverride;
           steeroverride = sf.steeroverride;
       }
       public void planRoute(VCS vcs,SensorFusion sf, String routename){
           if(acceloverride || brakeoverride || steeroverride){
               if(active){
                   display();
               }
           }else{
               if(active){
                   if(routename.equals("")){
                       if(routeIndex < currentRoute.length){
                        String currentInstruction = currentRoute[routeIndex];
                        String[] breakdown = currentInstruction.split(" ");
                        if(breakdown[0].equals("accelerate")){
                            System.out.println("Automatically accelerating by " + breakdown[1]);
                            float value = Float.parseFloat(breakdown[1]);
                            vcs.accelerate(value);
                        }else if(breakdown[0].equals("brake")){
                            System.out.println("You have arrived at your destination; automatically braking");
                            vcs.brake();
                            display();
                        }else{
                            System.out.println("Automatically turning "+breakdown[1]);
                            if(breakdown[1].equals("right")){
                                vcs.steer(-1);
                            }else{
                                vcs.steer(1);
                            }
                        }
                        routeIndex++;
                       }else{
                           display();
                       }
                       
                   }else{
                       for(int i = 0; i < routeData.size(); i++){
                           if(routeData.get(i)[0].equals(routename)){
                               currentRoute = routeData.get(i);
                               routeIndex = 1;
                               try {
                                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                                logs.println("Route Set");
                                logs.close();
                               } catch (Exception e) {
                               }
                               break;
                           }
                       }
                       if(currentRoute == null){
                        System.out.println("Route not found");
                        try {
                         PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                         logs.println("Could not find requested route");
                         logs.close();
                        } catch (Exception e) {
                        }
                        display();
                        }                   
                   }
               }
           }
       }
       public void display(){
           if(active){
            active = false;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("GPS: true\n", "GPS: false\n");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("GPS Navigation Deactivated");
                logs.close();

            } catch (Exception e) {
            }
        }else{
            active = true;
            try {
                String buttonString = "";
                FileInputStream buttons = new FileInputStream("./Data/buttons");
                
                int c;
                while ((c = buttons.read()) != -1){
                    buttonString += (char)c;
                }
                buttons.close();
                String updatedString = buttonString.replace("GPS: false\n", "GPS: true\n");
                PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                buttonupdate.printf("%s",updatedString);
                buttonupdate.close();
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                logs.println("GPS Navigation Activated");
                logs.close();

            } catch (Exception e) {
            }
        }
       }
   }