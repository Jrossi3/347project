import java.util.*;

public class Planning{
 public static void main(String[] args) {
     //Repeatedly runs until car is off
        while(true){
            //Get sensor fusion values
            CruiseControl cc = new CruiseControl();
            AdaptiveCruiseControl acc = new AdaptiveCruiseControl();
            AutomaticBraking ab = new AutomaticBraking();
            AutomaticSteering as = new AutomaticSteering();
            GPSNavigation gps = new GPSNavigation();
            if (cc.override != true && cc.active == true){
                //vcs
            }
            if (acc.override != true && acc.active == true){
                //vcs
            }
            if (ab.override != true && ab.active == true){
                //vcs
            }
            if (as.override != true && as.active == true){
                //vcs
            }
            if (gps.override != true && gps.active == true){
                //vcs
            }

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
class Direction{
    String direction;
    int time;
    Direction(String direction,int time){
        this.direction = direction;
        this.time = time;
    }
}
class CruiseControl{
    int speed;
    boolean override;
    boolean active;
    public void requestCurrentSpeed(){
        //Return sensor fusion value instead
        speed = 1;
        override = false;
        active = true;
    }
    public void planSpeed(){

    }
    public void display(){

    }
}
class AdaptiveCruiseControl{
    int streetSpeed;
    boolean override;
    boolean active;
    public void requestStreetSpeed(){
        //Return sensor fusion value instead        
        streetSpeed = 1;
        override = false;
        active = true;
    }
    public void planSpeed(){

    }
    public void display(){

    }
}
class AutomaticBraking{
   Vector<Obstacle> obstacleData; 
   boolean override;
   boolean active;
   public void requestObstacleData(){
       //get from sensor fusion
       obstacleData.add(new Obstacle("person", new int[]{1,1,1}));
       override = false;
       active = true;
   }
    public void planBraking(){

    }
    public void display(){
        
    }
}
class AutomaticSteering{
    Vector<Obstacle> obstacleData; 
    boolean override;
    boolean active;
    public void requestObstacleData(){
        //get from sensor fusion
        obstacleData.add(new Obstacle("person", new int[]{1,1,1}));
        override = false;
        active = true;
    }
     public void planSteering(){
 
     }
     public void display(){
 
     }
}
class GPSNavigation{
    Vector<Direction> routeData;
    boolean override;
    boolean active;
    public void requestRouteData(){
        //get from sensor fusion
        routeData.add(new Direction("Turn Left",4));
        override = false;
        active = true;
    }
    public void planRoute(){

    }
    public void display(){

    }
}