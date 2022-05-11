import java.io.*;
public class SystemManagement {
    static void displayOnPC(String text, boolean clear){
        if(clear){
            System.out.println("\033[H\033[2J");
            System.out.flush();
        }
        System.out.println(text);
    }
    static boolean sendLogin(String id, String pd){
        
        if (id.equals("UserID") && pd.equals("Password")){
            return true;
        }else{
            return false;
        }
    }
    static void getLogs(){
        FileInputStream vehiclelogs = null;
        FileOutputStream logscopy = null;
        try{
            vehiclelogs = new FileInputStream("./Data/logs");
            logscopy = new FileOutputStream("./logs.txt");
            int c;
            while((c = vehiclelogs.read()) != -1){
                logscopy.write(c);
            }
            vehiclelogs.close();
            logscopy.close();
        }catch(Exception e){
            
        }

        
    }
    static boolean checkUpdate(){
        FileInputStream CheckCloud = null;
        FileInputStream CheckCar = null;
        try{
            CheckCloud = new FileInputStream("./Cloud/cloudVersion");
            CheckCar = new FileInputStream("./Data/carVersion");
            int cloudchar;
            int carchar;
            String lineCloud = "";
            String lineCar = "";
            while((cloudchar = CheckCloud.read()) != -1){
                
                lineCloud += (char)cloudchar;
            }
            while((carchar = CheckCar.read()) != -1){
                lineCar += (char)carchar;
            }
            String vNumCloud = lineCloud.substring(lineCloud.indexOf("Version ") + (new String("Version ")).length());
            String vNumCar = lineCar.substring(lineCar.indexOf("Version ") + (new String("Version ")).length());
            float VersionCloud = Float.parseFloat(vNumCloud);
            float VersionCar = Float.parseFloat(vNumCar);
            CheckCloud.close();
            CheckCar.close();
            if(VersionCloud > VersionCar){
                displayOnPC("Update available.", true);
                displayOnPC(lineCloud.substring(lineCloud.indexOf("Version")), false);
                return true;
            }else{
                return false;
            } 

        }catch(Exception e){

        }
        return false;
    }
    static void downloadUpdate(){
        FileInputStream cloud = null;
        FileOutputStream localcopy = null;
        try {
            cloud = new FileInputStream("./Cloud/cloudVersion");
            localcopy = new FileOutputStream("./update");
            int c;
            while((c = cloud.read()) != -1){
                localcopy.write(c);
            }
            cloud.close();
            localcopy.close();
            Thread.sleep(1000);

        } catch (Exception e) {
            
        }
    }
    static void InstallUpdate(){
        FileInputStream localcopy = null;
        FileOutputStream car = null;
        try {
            localcopy = new FileInputStream("./update");
            car = new FileOutputStream("./Data/carVersion");
            int c;
            while((c = localcopy.read()) != -1){
                car.write(c);
            }
            localcopy.close();
            car.close();
            Thread.sleep(1000);

        } catch (Exception e) {
            
        }
    }
    public static void main(String[] args) {
        //Login message
        displayOnPC("IoT HTL System Management Interface: Login",true);
        Console Display = System.console();
        String username = Display.readLine("UserID: "); 
        String password = Display.readLine("Password: ");
        //Check if correct, if not then reprompt
        while(sendLogin(username,password) == false){
            displayOnPC("Incorrect UserID/Password.",true);
            username = Display.readLine("UserID: "); 
            password = Display.readLine("Password: ");
        }
        displayOnPC("Successfully Logged in\n", true);
        //Give options
        String input = "null";
        while(input.equals("Quit") == false){
            displayOnPC("Logs: Get Logs\nUpdate: Check for, Download and Install Update\nQuit: Exit Program",false);
            input = Display.readLine("Select an option: ");
            if(input.equals("Logs")){
                displayOnPC("Getting Logs...",true);
                getLogs();
                displayOnPC("Logs received and copied to current directory\n", true);
            }else if(input.equals("Update")){
                displayOnPC("Checking for Updates...", true);
                try{
                    Thread.sleep(2000);
                }catch(Exception e){
                    //Shouldn't happen
                }
                if (checkUpdate()){
                    while(true){
                        String response = Display.readLine("Would you like to install this update? (Yes/No): ");
                        if (response.equals("Yes")){
                            displayOnPC("Downloading updates...", true);
                            downloadUpdate();
                            displayOnPC("Installing updates...", false);
                            InstallUpdate();
                            displayOnPC("Update installed\n", true);
                            break;
                        }else if(response.equals("No")){
                            displayOnPC("Update not installed\n", true);
                            break;
                        }else{
                            displayOnPC("Invalid Response Given\n", true);
                        }
                    }
                    
                }else{
                    displayOnPC("No updates available at this time.\n", false);
                }

            }else{
                displayOnPC("Incorrect input or input not provided\n", true);
            }
        }
        displayOnPC("Logging Off...",true);
        

    }
}
