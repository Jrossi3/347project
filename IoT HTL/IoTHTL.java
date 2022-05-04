import java.util.*;
import java.io.*;

public class IoTHTL {
    public static void main(String[] args) {
        Planning plan = new Planning();
        VCS VCS = new VCS();
        SensorFusion sf = new SensorFusion();
        Console input = System.console();
        while (true){
            System.out.println("Current Speed: " + sf.getCurrentspeed());
            String cmd = input.readLine("> ");
            String argarray[] = cmd.split(" ");
            try {
                PrintStream speedReset = new PrintStream(new FileOutputStream("./Data/speed"));
                speedReset.println("0.0");
                speedReset.close();
            } catch (Exception e) {
            }
            if(argarray[0].equals("") && argarray.length == 1){
                plan.Plan(VCS,sf);
            }else if(argarray[0].equals("accelerate") && argarray.length == 2){
                //accelerate (User override)
                sf.override = true;
                float speedinput = Float.parseFloat(argarray[1]);
                VCS.accelerate(speedinput);
            }else if(argarray[0].equals("steer") && argarray.length == 2){
                //steer (User override)
                sf.override = true;
                if(argarray[1].equals("left")){
                    VCS.steer(1);
                }else if(argarray[1].equals("right")){
                    VCS.steer(-1);
                }
            }else if(argarray[0].equals("brake") && argarray.length == 1){
                //brake (user override)
                sf.override = true;
                VCS.brake();
            }else if(argarray[0].equals("cc") && argarray.length == 1){
                try {
                    String buttonString = "";
                    FileInputStream buttons = new FileInputStream("./Data/buttons");
                    
                    int c;
                    while ((c = buttons.read()) != -1){
                        buttonString += (char)c;
                    }
                    buttons.close();
                    buttonString.replace("CC: false", "CC: true");
                    PrintStream buttonupdate = new PrintStream(new FileOutputStream("./Data/buttons"));
                    buttonupdate.printf("%s",buttonString);
                    buttonupdate.close();
                    PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs",true));
                    logs.println("Cruise Control Activated");
                    logs.close();

                } catch (Exception e) {
                }
            }else if(argarray[0].equals("acc") && argarray.length == 1){
            }else if(argarray[0].equals("ab") && argarray.length == 1){
            }else if(argarray[0].equals("as") && argarray.length == 1){
            }else if(argarray[0].equals("gps")){
            }else{
                System.out.println("Incorrect command or incorrect number of args");
                continue;
            }
            plan.Plan(VCS, sf);
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
                PrintStream logs = new PrintStream(new FileOutputStream("./Data/logs"));
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
    Vector<Obstacle> objects;
    int streetSpeedLimit;
    float carPosition[] = new float[3];
    boolean sensorMonitoring = true;
    boolean override = false;
    public float getCurrentspeed() {
        FileInputStream speedSensor = null;
        String speedString = "";
        try {
            speedSensor = new FileInputStream("./Data/speed");
            int c;
            while((c =  speedSensor.read()) != -1){
                speedString += (char)c;
            }
        } catch (Exception e) {
            
        }
        float speed = Float.parseFloat(speedString);
        return speed;
    }
    public float getStreetSpeed() {
        FileInputStream speedSensor = null;
        String speedString = "";
        try {
            speedSensor = new FileInputStream("./Data/speed");
            int c;
            while((c =  speedSensor.read()) != -1){
                speedString += (char)c;
            }
        } catch (Exception e) {
            
        }
        float speed = Float.parseFloat(speedString);
        return speed;
    }
    public Vector<Obstacle> getObstacleData() {
        return objects;
    }
    public Vector<Direction> getRouteData(){
        Vector<Direction> RouteData = new Vector<Direction>();
        RouteData.add(new Direction("test", 1));
        return RouteData;
    }

}

class Planning{
    void Plan(VCS VCS, SensorFusion sf) {
        CruiseControl cc = new CruiseControl();
        AdaptiveCruiseControl acc = new AdaptiveCruiseControl();
        AutomaticBraking ab = new AutomaticBraking();
        AutomaticSteering as = new AutomaticSteering();
        GPSNavigation gps = new GPSNavigation();
           //Get sensor fusion values
            cc.requestCurrentSpeed(sf);
            acc.requestStreetSpeed(sf);
            ab.requestObstacleData(sf);
            as.requestObstacleData(sf);
            gps.requestRouteData(sf);
            cc.planSpeed(VCS);
            acc.planSpeed(VCS);
            ab.planBraking(VCS);
            as.planSteering(VCS);
            gps.planRoute(VCS);
       }
   }
   
class Direction{
       String direction;
       int time;
       Direction(String direction,int time){
           this.direction = direction;
           this.time = time;
       }
   }
class Obstacle{
    String type;
    int[] position = new int[3];
    Obstacle(String type, int[] position){
        this.type = type;
        for(int i = 0; i < 3; i++){
            this.position[i] = position[i];
        }
       }
   }
class Instruction{
    String request;
    int value;
    Instruction(String request, int value){
        this.request = request;
        this.value = value;
    }
}
class CruiseControl{
    float speed;
    boolean override;
    boolean active;
    public void requestCurrentSpeed(SensorFusion sf){
        //Return sensor fusion value instead
        speed = sf.getCurrentspeed();
        override = sf.override;
        active = true;
    }
    public void planSpeed(VCS vcs){

    }
    public void display(){
   
    }
}
class AdaptiveCruiseControl{
    float streetSpeed;
    boolean override;
    boolean active;
    public void requestStreetSpeed(SensorFusion sf){
        //Return sensor fusion value instead        
        streetSpeed = sf.getStreetSpeed();
        override = sf.override;
        active = true;
    }
    public void planSpeed(VCS vcs){
   
    }
    public void display(){
   
    }
}
class AutomaticBraking{
    Vector<Obstacle> obstacleData; 
    boolean override;
    boolean active;
    public void requestObstacleData(SensorFusion sf){
        //get from sensor fusion
        obstacleData = sf.getObstacleData();
        override = sf.override;
        active = true;
    }
    public void planBraking(VCS vcs){
   
    }
    public void display(){
     
    }
}
   class AutomaticSteering{
       Vector<Obstacle> obstacleData; 
       boolean override;
       boolean active;
       public void requestObstacleData(SensorFusion sf){
           //get from sensor fusion
           obstacleData = sf.getObstacleData();
           override = sf.override;
           active = true;
       }
        public void planSteering(VCS vcs){
    
        }
        public void display(){
    
        }
   }
   class GPSNavigation{
       Vector<Direction> routeData;
       boolean override;
       boolean active;
       public void requestRouteData(SensorFusion sf){
           //get from sensor fusion
           routeData = sf.getRouteData();
           override = sf.override;
           active = true;
       }
       public void planRoute(VCS vcs){
   
       }
       public void display(){
   
       }
   }



