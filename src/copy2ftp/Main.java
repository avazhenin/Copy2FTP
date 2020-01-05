/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copy2ftp;

import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author vazhenin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        new Worker(args[0]).run();
    }
    
}
